package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.exception.ErrorType.CONTEXT
import com.procurement.contracting.exception.ErrorType.CONTRACT_STATUS_DETAILS
import com.procurement.contracting.model.dto.*
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.model.dto.ocds.*
import com.procurement.contracting.model.entity.AcEntity
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toLocalDateTime
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class FinalUpdateService(private val acDao: AcDao,
                         private val generationService: GenerationService,
                         private val templateService: TemplateService) {

    fun finalUpdate(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val ocId = cm.context.ocid ?: throw ErrorException(CONTEXT)
        val country = cm.context.country ?: throw ErrorException(CONTEXT)
        val language = cm.context.language ?: throw ErrorException(CONTEXT)
        val pmd = cm.context.pmd ?: throw ErrorException(CONTEXT)
        val dto = toObject(FinalUpdateAcRq::class.java, cm.data)

        val entity = acDao.getByCpIdAndAcId(cpId, ocId)
        val contractProcess = toObject(ContractProcess::class.java, entity.jsonData)
//        if (!(contractProcess.contract.status == ContractStatus.PENDING && contractProcess.contract.statusDetails == ContractStatusDetails.ISSUED))
//            throw ErrorException(CONTRACT_STATUS_DETAILS)//BR-9.5.1

        //    Proceeds Documents object from Request by rule BR-9.5.2;
        dto.documents.forEach { document ->
            contractProcess.contract.documents?.plus(element = DocumentContract(
                id = document.id,
                documentType = DocumentTypeContract.CONTRACT_SIGNED,
                title = null,
                description = null,
                relatedLots = null))
        }

        val buyer = contractProcess.buyer ?: throw ErrorException(ErrorType.BUYER_IS_EMPTY)
        val supplier = contractProcess.award.suppliers.first()
        val buyerMilestone = generateBuyerMilestone(buyer, country, pmd, language)
        val supplierMilestone = generateSupplierMilestone(supplier, country, pmd, language)
        val activationMilestone = generateContractActivationMilestone(buyer, country, pmd, language)

        val milestones: List<Milestone> = contractProcess.contract.milestones
            ?: mutableListOf()
        milestones.plus(buyerMilestone)
        milestones.plus(supplierMilestone)
        milestones.plus(activationMilestone)

        if (contractProcess.treasuryBudgetSources != null) {
            val validationMilestone = generateValidationMilestone(country, pmd, language)
            milestones.plus(validationMilestone)
        }
        val confirmationRequest = contractProcess.contract.confirmationRequests ?: mutableListOf()
        val confirmationRequestBuyer = generateBuyerConfirmationRequest(buyer, country, pmd, language, dto.documents.first().id)
        confirmationRequest.plus(confirmationRequestBuyer)
        contractProcess.contract.statusDetails = ContractStatusDetails.APPROVEMENT
        entity.jsonData = toJson(contractProcess)
        acDao.save(entity)
        return ResponseDto(data = FinalUpdateAcRs(contractProcess.contract))
    }

    private fun generateBuyerMilestone(buyer: OrganizationReferenceBuyer, country: String, pmd: String, language: String): Milestone {

        val template = templateService.getConfirmationRequestTemplate(
            country = country,
            pmd = pmd,
            language = language,
            templateId = "cs-buyer-approval-on")

        val relatedParties: List<RelatedParty> = listOf(RelatedParty(id = buyer.id, name = buyer.name
            ?: throw ErrorException(ErrorType.BUYER_NAME_IS_EMPTY)))

        val milestone = Milestone(
            id = "approval-" + relatedParties.first().id + generationService.generateTimeBasedUUID(),
            title = template.title,
            description = template.description,
            status = MilestoneStatus.SCHEDULED,
            type = MilestoneType.APPROVAL,
            relatedItems = null,
            additionalInformation = "",
            dueDate = LocalDateTime.now(),
            relatedParties = relatedParties
        )
        return milestone

    }

    private fun generateSupplierMilestone(supplier: OrganizationReferenceSupplier, country: String, pmd: String, language: String): Milestone {

        val template = templateService.getConfirmationRequestTemplate(
            country = country,
            pmd = pmd,
            language = language,
            templateId = "cs-supplier-approval-on")

        val relatedParties: List<RelatedParty> = listOf(RelatedParty(id = supplier.id, name = supplier.name))

        val milestone = Milestone(
            id = "approval-" + relatedParties.first().id + generationService.generateTimeBasedUUID(),
            title = template.title,
            description = template.description,
            status = MilestoneStatus.SCHEDULED,
            type = MilestoneType.APPROVAL,
            relatedItems = null,
            additionalInformation = "",
            dueDate = LocalDateTime.now(),
            relatedParties = relatedParties
        )
        return milestone

    }

    private fun generateContractActivationMilestone(buyer: OrganizationReferenceBuyer, country: String, pmd: String, language: String): Milestone {

        val template = templateService.getConfirmationRequestTemplate(
            country = country,
            pmd = pmd,
            language = language,
            templateId = "cs-buyer-activate-on")

        val relatedParties: List<RelatedParty> = listOf(RelatedParty(id = buyer.id, name = buyer.name
            ?: throw ErrorException(ErrorType.BUYER_NAME_IS_EMPTY)))

        val milestone = Milestone(
            id = "approval-" + relatedParties.first().id + generationService.generateTimeBasedUUID(),
            title = template.title,
            description = template.description,
            status = MilestoneStatus.SCHEDULED,
            type = MilestoneType.APPROVAL,
            relatedItems = null,
            additionalInformation = "",
            dueDate = LocalDateTime.now(),
            relatedParties = relatedParties
        )
        return milestone

    }

    private fun generateValidationMilestone(country: String, pmd: String, language: String): Milestone {

        val template = templateService.getConfirmationRequestTemplate(
            country = country,
            pmd = pmd,
            language = language,
            templateId = "cs-treasury-validate-on")

        val relatedParties: List<RelatedParty> = listOf(RelatedParty(id = "hardcodeID!", name = "hardcodeNAME!"))

        val milestone = Milestone(
            id = "approval-" + relatedParties.first().id + generationService.generateTimeBasedUUID(),
            title = template.title,
            description = template.description,
            status = MilestoneStatus.SCHEDULED,
            type = MilestoneType.APPROVAL,
            relatedItems = null,
            additionalInformation = "",
            dueDate = LocalDateTime.now(),
            relatedParties = relatedParties
        )
        return milestone

    }

    private fun generateBuyerConfirmationRequest(buyer: OrganizationReferenceBuyer, country: String, pmd: String, language: String, documentId: String): ConfirmationRequest {
        val template = templateService.getConfirmationRequestTemplate(
            country = country,
            pmd = pmd,
            language = language,
            templateId = "cs-buyer-confirmation-on")

        var requestDescription = ""
        if (language == "EN") {
            requestDescription = template.description

        }

        val relatedPerson = getAuthorityOrganizationPersonBuyer(buyer)
        val request = Request(id = template.id + documentId + "-" + relatedPerson.id,
            title = template.requestTitle + relatedPerson.name,
            description = requestDescription,
            relatedPerson = relatedPerson
        )

        val requestGroup = RequestGroup(
            id = template.id + documentId + "-" + buyer.identifier?.id,
            requests = hashSetOf(request)
        )

        var confirmationRequest = ConfirmationRequest(
            id = template.id + documentId,
            relatedItem = documentId,
            source = "buyer",
            type = null,
            title = null,
            description = null,
            relatesTo = template.relatesTo,
            requestGroups = hashSetOf(requestGroup))

        if (language == "EN") {
            confirmationRequest.description = template.description
            confirmationRequest.title = template.title
        }
        return confirmationRequest

    }

    private fun generateSupplierConfirmationRequest(supplier: OrganizationReferenceSupplier, country: String, pmd: String, language: String, documentId: String): ConfirmationRequest {
        val template = templateService.getConfirmationRequestTemplate(
            country = country,
            pmd = pmd,
            language = language,
            templateId = "cs-tenderer-confirmation-on")

        var requestDescription = ""
        if (language == "EN") {
            requestDescription = template.description

        }

        val relatedPerson = getAuthorityOrganizationPersonSupplier(supplier)
        val request = Request(id = template.id + documentId + "-" + relatedPerson.id,
            title = template.requestTitle + relatedPerson.name,
            description = requestDescription,
            relatedPerson = relatedPerson
        )

        val requestGroup = RequestGroup(
            id = template.id + documentId + "-" + supplier.identifier.id,
            requests = hashSetOf(request)
        )

        var confirmationRequest = ConfirmationRequest(
            id = template.id + documentId,
            relatedItem = documentId,
            source = "tenderer",
            type = null,
            title = null,
            description = null,
            relatesTo = template.relatesTo,
            requestGroups = hashSetOf(requestGroup))

        if (language == "EN") {
            confirmationRequest.description = template.description
            confirmationRequest.title = template.title
        }
        return confirmationRequest

    }

    private fun getAuthorityOrganizationPersonBuyer(buyer: OrganizationReferenceBuyer): RelatedPerson {

        for (person in buyer.persones) {

            val id: String? = person.businessFunctions.firstOrNull { it.type == "authority" }?.id

            if (id != null)
                return RelatedPerson(id = person.identifier.id, name = person.name)
        }
        throw ErrorException(ErrorType.BUYER_NAME_IS_EMPTY)

    }

    private fun getAuthorityOrganizationPersonSupplier(supplier: OrganizationReferenceSupplier): RelatedPerson {

        val persones = supplier.persones
        if (persones != null && persones.isNotEmpty()) {
            for (person in persones) {

                val id: String? = person.businessFunctions.firstOrNull { it.type == "authority" }?.id

                if (id != null)
                    return RelatedPerson(id = person.identifier.id, name = person.name)
            }
        }

        throw ErrorException(ErrorType.BUYER_NAME_IS_EMPTY)

    }


}
