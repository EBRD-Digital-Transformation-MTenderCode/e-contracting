package com.procurement.contracting.application.service

import com.procurement.contracting.application.exception.repository.SaveEntityException
import com.procurement.contracting.application.repository.ac.AwardContractRepository
import com.procurement.contracting.application.repository.ac.model.AwardContractEntity
import com.procurement.contracting.application.repository.model.ContractProcess
import com.procurement.contracting.domain.model.ProcurementMethodDetails
import com.procurement.contracting.domain.model.ac.id.asAwardContractId
import com.procurement.contracting.domain.model.ac.status.AwardContractStatus
import com.procurement.contracting.domain.model.ac.status.AwardContractStatusDetails
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestSource
import com.procurement.contracting.domain.model.document.type.DocumentTypeContract
import com.procurement.contracting.domain.model.milestone.status.MilestoneStatus
import com.procurement.contracting.domain.model.milestone.type.MilestoneSubType
import com.procurement.contracting.domain.model.milestone.type.MilestoneType
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.exception.ErrorType.INVALID_BUSINESS_FUNCTIONS_TYPE
import com.procurement.contracting.infrastructure.handler.v1.CommandMessage
import com.procurement.contracting.infrastructure.handler.v1.country
import com.procurement.contracting.infrastructure.handler.v1.cpid
import com.procurement.contracting.infrastructure.handler.v1.language
import com.procurement.contracting.infrastructure.handler.v1.model.request.Document
import com.procurement.contracting.infrastructure.handler.v1.model.request.FinalUpdateAcRq
import com.procurement.contracting.infrastructure.handler.v1.model.request.FinalUpdateAcRs
import com.procurement.contracting.infrastructure.handler.v1.ocid
import com.procurement.contracting.infrastructure.handler.v1.pmd
import com.procurement.contracting.model.dto.ocds.ConfirmationRequest
import com.procurement.contracting.model.dto.ocds.DocumentContract
import com.procurement.contracting.model.dto.ocds.Milestone
import com.procurement.contracting.model.dto.ocds.OrganizationReferenceBuyer
import com.procurement.contracting.model.dto.ocds.OrganizationReferenceSupplier
import com.procurement.contracting.model.dto.ocds.RelatedParty
import com.procurement.contracting.model.dto.ocds.RelatedPerson
import com.procurement.contracting.model.dto.ocds.Request
import com.procurement.contracting.model.dto.ocds.RequestGroup
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service

@Service
class FinalUpdateService(
    private val acRepository: AwardContractRepository,
    private val generationService: GenerationService,
    private val templateService: TemplateService
) {

    fun finalUpdate(cm: CommandMessage): FinalUpdateAcRs {
        val cpid = cm.cpid
        val ocid = cm.ocid
        val country = cm.country
        val language = cm.language
        val pmd = cm.pmd
        val dto = toObject(FinalUpdateAcRq::class.java, cm.data)

        val awardContractId = ocid.asAwardContractId()
        val entity: AwardContractEntity = acRepository.findBy(cpid, awardContractId)
            .orThrow { it.exception }
            ?: throw ErrorException(ErrorType.CONTRACT_NOT_FOUND)
        val contractProcess = toObject(ContractProcess::class.java, entity.jsonData)

        if (contractProcess.contract.status != AwardContractStatus.PENDING) throw ErrorException(ErrorType.CONTRACT_STATUS)
        if (contractProcess.contract.statusDetails != AwardContractStatusDetails.ISSUED) throw ErrorException(ErrorType.CONTRACT_STATUS_DETAILS)

        contractProcess.contract.apply {
            documents = addContractDocuments(dto.documents, documents)
        }
        val buyer = contractProcess.buyer ?: throw ErrorException(ErrorType.BUYER_IS_EMPTY)
        val supplier = contractProcess.award.suppliers.first()
        val buyerMilestone = generateBuyerMilestone(buyer, country, pmd, language)
        val supplierMilestone = generateSupplierMilestone(supplier, country, pmd, language)
        val activationMilestone = generateContractActivationMilestone(buyer, country, pmd, language)

        val milestones = contractProcess.contract.milestones ?: mutableListOf()
        milestones.add(buyerMilestone)
        milestones.add(supplierMilestone)
        milestones.add(activationMilestone)

        if (contractProcess.treasuryBudgetSources != null) {
            val validationMilestone = generateValidationMilestone(country, pmd, language)
            milestones.add(validationMilestone)
        }
        contractProcess.contract.milestones = milestones

        val confirmationRequests = contractProcess.contract.confirmationRequests ?: mutableListOf()
        val confirmationRequestBuyer = generateBuyerConfirmationRequest(buyer, country, pmd, language, dto.documents.first().id)
        confirmationRequests.add(confirmationRequestBuyer)
        contractProcess.contract.confirmationRequests = confirmationRequests

        contractProcess.contract.statusDetails = AwardContractStatusDetails.APPROVEMENT

        val updatedContractEntity = entity.copy(
            status = contractProcess.contract.status,
            statusDetails = contractProcess.contract.statusDetails,
            jsonData = toJson(contractProcess)
        )

        val wasApplied = acRepository
            .updateStatusesAC(
                cpid = cpid,
                id = updatedContractEntity.id,
                status = updatedContractEntity.status,
                statusDetails = updatedContractEntity.statusDetails,
                jsonData = updatedContractEntity.jsonData
            )
            .orThrow { it.exception }
        if (!wasApplied)
            throw SaveEntityException(message = "An error occurred when writing a record(s) of the save updated AC by cpid '${cpid}' and id '${updatedContractEntity.id}' with status '${updatedContractEntity.status}' and status details '${updatedContractEntity.statusDetails}' to the database. Record is not exists.")

        return FinalUpdateAcRs(contractProcess.contract)
    }

    private fun addContractDocuments(documentsDto: List<Document>, documentsDb: List<DocumentContract>?): List<DocumentContract>? {
        //update
        val newDocuments = documentsDto.map {
            DocumentContract(
                id = it.id,
                documentType = DocumentTypeContract.CONTRACT_SIGNED,
                title = null,
                description = null,
                relatedLots = null
            )
        }

        return if (documentsDb != null && documentsDb.isNotEmpty()) (documentsDb + newDocuments)
        else newDocuments
    }

    private fun generateBuyerMilestone(buyer: OrganizationReferenceBuyer, country: String, pmd: ProcurementMethodDetails, language: String): Milestone {

        val template = templateService.getConfirmationRequestTemplate(
                country = country,
                pmd = pmd,
                language = language,
                templateId = "cs-buyer-approval-on")

        val relatedParties: List<RelatedParty> = listOf(RelatedParty(id = buyer.id, name = buyer.name
                ?: throw ErrorException(ErrorType.BUYER_NAME_IS_EMPTY)))

        return Milestone(
            id = "approval-" + relatedParties.first().id + generationService.generateTimeBasedUUID(),
            title = template.title,
            description = template.description,
            status = MilestoneStatus.SCHEDULED,
            type = MilestoneType.APPROVAL,
            subtype = MilestoneSubType.BUYERS_APPROVAL,
            relatedItems = null,
            additionalInformation = null,
            dueDate = null,
            relatedParties = relatedParties
        )
    }

    private fun generateSupplierMilestone(supplier: OrganizationReferenceSupplier, country: String, pmd: ProcurementMethodDetails, language: String): Milestone {

        val template = templateService.getConfirmationRequestTemplate(
                country = country,
                pmd = pmd,
                language = language,
                templateId = "cs-supplier-approval-on")

        val relatedParties: List<RelatedParty> = listOf(RelatedParty(id = supplier.id, name = supplier.name))

        return Milestone(
            id = "approval-" + relatedParties.first().id + generationService.generateTimeBasedUUID(),
            title = template.title,
            description = template.description,
            status = MilestoneStatus.SCHEDULED,
            type = MilestoneType.APPROVAL,
            subtype = MilestoneSubType.SUPPLIERS_APPROVAL,
            relatedItems = null,
            additionalInformation = null,
            dueDate = null,
            relatedParties = relatedParties
        )
    }

    private fun generateContractActivationMilestone(buyer: OrganizationReferenceBuyer, country: String, pmd: ProcurementMethodDetails, language: String): Milestone {

        val template = templateService.getConfirmationRequestTemplate(
                country = country,
                pmd = pmd,
                language = language,
                templateId = "cs-buyer-activate-on")

        val relatedParties: List<RelatedParty> = listOf(RelatedParty(id = buyer.id, name = buyer.name
                ?: throw ErrorException(ErrorType.BUYER_NAME_IS_EMPTY)))

        return Milestone(
            id = "approval-" + relatedParties.first().id + generationService.generateTimeBasedUUID(),
            title = template.title,
            description = template.description,
            status = MilestoneStatus.SCHEDULED,
            type = MilestoneType.APPROVAL,
            subtype = MilestoneSubType.CONTRACT_ACTIVATION,
            relatedItems = null,
            additionalInformation = null,
            dueDate = null,
            relatedParties = relatedParties
        )
    }

    private fun generateValidationMilestone(country: String, pmd: ProcurementMethodDetails, language: String): Milestone {

        val template = templateService.getConfirmationRequestTemplate(
                country = country,
                pmd = pmd,
                language = language,
                templateId = "cs-treasury-validate-on")

        val relatedParties: List<RelatedParty> = listOf(RelatedParty(id = "approveBodyId", name = "approveBodyName"))

        return Milestone(
            id = "approval-" + relatedParties.first().id + generationService.generateTimeBasedUUID(),
            title = template.title,
            description = template.description,
            status = MilestoneStatus.SCHEDULED,
            type = MilestoneType.APPROVAL,
            subtype = MilestoneSubType.APPROVE_BODY_VALIDATION,
            relatedItems = null,
            additionalInformation = null,
            dueDate = null,
            relatedParties = relatedParties
        )
    }

    private fun generateBuyerConfirmationRequest(buyer: OrganizationReferenceBuyer, country: String, pmd: ProcurementMethodDetails, language: String, documentId: String): ConfirmationRequest {
        val template = templateService.getConfirmationRequestTemplate(
                country = country,
                pmd = pmd,
                language = language,
                templateId = "cs-buyer-confirmation-on")
        val relatedPerson = getAuthorityOrganizationPersonBuyer(buyer)
        val request = Request(id = template.id + documentId + "-" + relatedPerson.id,
                title = template.requestTitle + relatedPerson.name,
                description = template.requestDescription,
                relatedPerson = relatedPerson
        )
        val requestGroup = RequestGroup(
                id = template.id + documentId + "-" + buyer.identifier?.id,
                requests = listOf(request)
        )
        return ConfirmationRequest(
            id = template.id + documentId,
            relatedItem = documentId,
            source = ConfirmationRequestSource.BUYER,
            type = template.type,
            title = template.title,
            description = template.description,
            relatesTo = template.relatesTo,
            requestGroups = listOf(requestGroup)
        )
    }

    private fun getAuthorityOrganizationPersonBuyer(buyer: OrganizationReferenceBuyer): RelatedPerson {
        for (person in buyer.persones) {
            val id: String? = person.businessFunctions.firstOrNull { it.type == "authority" }?.id
            if (id != null)
                return RelatedPerson(id = person.identifier.id, name = person.name)
        }
        throw ErrorException(INVALID_BUSINESS_FUNCTIONS_TYPE)
    }
}
