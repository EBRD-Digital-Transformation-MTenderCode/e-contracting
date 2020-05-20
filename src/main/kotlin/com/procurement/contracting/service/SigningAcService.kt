package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestSource
import com.procurement.contracting.domain.model.confirmation.response.ConfirmationResponseType
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails
import com.procurement.contracting.domain.model.document.type.DocumentTypeContract
import com.procurement.contracting.domain.model.milestone.status.MilestoneStatus
import com.procurement.contracting.domain.model.milestone.type.MilestoneSubType
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.exception.ErrorType.BUDGET_ALLOCATION
import com.procurement.contracting.exception.ErrorType.BUYER_NAME_IS_EMPTY
import com.procurement.contracting.exception.ErrorType.CONTEXT
import com.procurement.contracting.exception.ErrorType.INVALID_BUYER_ID
import com.procurement.contracting.exception.ErrorType.INVALID_CONFIRMATION_REQUEST_DATE
import com.procurement.contracting.exception.ErrorType.INVALID_OWNER
import com.procurement.contracting.exception.ErrorType.INVALID_RELATED_PERSON_ID
import com.procurement.contracting.exception.ErrorType.INVALID_REQUEST_ID
import com.procurement.contracting.exception.ErrorType.INVALID_SUPPLIER_ID
import com.procurement.contracting.exception.ErrorType.TREASURY_BUDGET_SOURCES
import com.procurement.contracting.model.dto.BuyerSigningRs
import com.procurement.contracting.model.dto.ConfirmationResponseRq
import com.procurement.contracting.model.dto.ContractProcess
import com.procurement.contracting.model.dto.ProceedResponseRq
import com.procurement.contracting.model.dto.SupplierSigningRs
import com.procurement.contracting.model.dto.TreasuryBudgetSourceSupplierSigning
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.ocds.ConfirmationRequest
import com.procurement.contracting.model.dto.ocds.ConfirmationResponse
import com.procurement.contracting.model.dto.ocds.ConfirmationResponseValue
import com.procurement.contracting.model.dto.ocds.DocumentContract
import com.procurement.contracting.model.dto.ocds.Milestone
import com.procurement.contracting.model.dto.ocds.OrganizationReferenceBuyer
import com.procurement.contracting.model.dto.ocds.OrganizationReferenceSupplier
import com.procurement.contracting.model.dto.ocds.RelatedPerson
import com.procurement.contracting.model.dto.ocds.Request
import com.procurement.contracting.model.dto.ocds.RequestGroup
import com.procurement.contracting.model.dto.ocds.Verification
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toLocalDateTime
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service
import java.math.RoundingMode

@Service
class SigningAcService(private val acDao: AcDao,
                       private val generationService: GenerationService,
                       private val templateService: TemplateService) {

    fun buyerSigningAC(cm: CommandMessage): BuyerSigningRs {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val ocId = cm.context.ocid ?: throw ErrorException(CONTEXT)
        val token = cm.context.token ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val country = cm.context.country ?: throw ErrorException(CONTEXT)
        val language = cm.context.language ?: throw ErrorException(CONTEXT)
        val pmd = cm.context.pmd ?: throw ErrorException(CONTEXT)
        val startDate = cm.context.startDate?.toLocalDateTime() ?: throw ErrorException(CONTEXT)
        val requestId = cm.context.id ?: throw ErrorException(CONTEXT)
        val dto = toObject(ProceedResponseRq::class.java, cm.data)

        val entity = acDao.getByCpIdAndAcId(cpId, ocId)
        if (entity.owner != owner) throw ErrorException(error = INVALID_OWNER)
        if (entity.token.toString() != token) throw ErrorException(ErrorType.INVALID_TOKEN)

        val contractProcess = toObject(ContractProcess::class.java, entity.jsonData)

//        if (contractProcess.contract.status != ContractStatus.PENDING) throw ErrorException(CONTRACT_STATUS) //VR-9.6.4
//        if (contractProcess.contract.statusDetails != ContractStatusDetails.APPROVEMENT) throw ErrorException(CONTRACT_STATUS_DETAILS)

        var isRequestIdPresent = false
        contractProcess.contract.confirmationRequests?.forEach { confirmationRequest ->
            confirmationRequest.requestGroups?.forEach { requestGroup ->
                requestGroup.requests.forEach {
                    if (it.id == requestId) isRequestIdPresent = true
                }
            }
        }
        if (!isRequestIdPresent) throw ErrorException(INVALID_REQUEST_ID)

        val buyer = contractProcess.buyer ?: throw ErrorException(ErrorType.BUYER_IS_EMPTY)
        if (dto.confirmationResponse.value.id != buyer.id) throw ErrorException(INVALID_BUYER_ID)//VR-9.6.5
        validateRelatedPersonId(contractProcess, dto, requestId)//9.6.6
        if (dto.confirmationResponse.value.date.isAfter(startDate)) throw ErrorException(INVALID_CONFIRMATION_REQUEST_DATE)//VR-9.6.7

        val confirmationResponse = generateBuyerConfirmationResponse(
                buyer = buyer,
                dto = dto.confirmationResponse,
                country = country,
                pmd = pmd,
                language = language,
                relatedPerson = getAuthorityOrganizationPerson(contractProcess, requestId),
                requestId = requestId
        )
        val confirmationResponses = contractProcess.contract.confirmationResponses ?: mutableListOf()
        confirmationResponses.add(confirmationResponse)

        val supplier = contractProcess.award.suppliers.first()
        val confirmationRequest = generateSupplierConfirmationRequest(
                supplier = supplier,
                country = country,
                pmd = pmd,
                language = language,
                verificationValue = dto.confirmationResponse.value.verification.first().value
        )
        val confirmationRequests = contractProcess.contract.confirmationRequests ?: mutableListOf()
        confirmationRequests.add(confirmationRequest)

        val document = DocumentContract(
            id = dto.confirmationResponse.value.verification.first().value,
            documentType = DocumentTypeContract.CONTRACT_SIGNED,
            relatedConfirmations = mutableListOf(confirmationResponse.id),
            title = null,
            description = null,
            relatedLots = null
        )

        val documents = contractProcess.contract.documents?.toMutableList() ?: mutableListOf()
        documents.add(document)

        contractProcess.contract.milestones?.asSequence()
                ?.filter { it.subtype == MilestoneSubType.BUYERS_APPROVAL }
                ?.forEach { milestone ->
                    milestone.apply {
                        dateModified = startDate
                        dateMet = confirmationResponse.value.date
                        status = MilestoneStatus.MET
                    }
                }

        contractProcess.contract.confirmationRequests = confirmationRequests
        contractProcess.contract.statusDetails = ContractStatusDetails.APPROVED
        contractProcess.contract.confirmationResponses = confirmationResponses
        contractProcess.contract.documents = documents

        entity.statusDetails = ContractStatusDetails.APPROVED
        entity.jsonData = toJson(contractProcess)
        acDao.save(entity)
        return BuyerSigningRs(contractProcess.contract)
    }

    fun supplierSigningAC(cm: CommandMessage): SupplierSigningRs {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val ocId = cm.context.ocid ?: throw ErrorException(CONTEXT)
        val country = cm.context.country ?: throw ErrorException(CONTEXT)
        val language = cm.context.language ?: throw ErrorException(CONTEXT)
        val pmd = cm.context.pmd ?: throw ErrorException(CONTEXT)
        val startDate = cm.context.startDate?.toLocalDateTime() ?: throw ErrorException(CONTEXT)
        val requestId = cm.context.id ?: throw ErrorException(CONTEXT)
        val dto = toObject(ProceedResponseRq::class.java, cm.data)

        val entity = acDao.getByCpIdAndAcId(cpId, ocId)
        val contractProcess = toObject(ContractProcess::class.java, entity.jsonData)

//        if (contractProcess.contract.status != ContractStatus.PENDING) throw ErrorException(CONTRACT_STATUS)
//        if (contractProcess.contract.statusDetails != ContractStatusDetails.APPROVED) throw ErrorException(CONTRACT_STATUS_DETAILS)

        var isRequestIdPresent = false
        contractProcess.contract.confirmationRequests?.forEach { confirmationRequest ->
            confirmationRequest.requestGroups?.forEach { requestGroup ->
                requestGroup.requests.forEach {
                    if (it.id == requestId) isRequestIdPresent = true
                }
            }
        }
        if (!isRequestIdPresent) throw ErrorException(INVALID_REQUEST_ID)
        if (dto.confirmationResponse.value.id != contractProcess.award.suppliers.first().id) throw ErrorException(INVALID_SUPPLIER_ID)
        validateRelatedPersonId(contractProcess, dto, requestId)
        if (dto.confirmationResponse.value.date.isAfter(startDate)) throw ErrorException(INVALID_CONFIRMATION_REQUEST_DATE)

        val supplier = contractProcess.award.suppliers.first()
        val confirmationResponse = generateSupplierConfirmationResponse(
                supplier = supplier,
                dto = dto.confirmationResponse,
                country = country,
                pmd = pmd,
                language = language,
                relatedPerson = getAuthorityOrganizationPerson(contractProcess, requestId),
                requestId = requestId
        )
        val confirmationResponses = contractProcess.contract.confirmationResponses ?: mutableListOf()
        confirmationResponses.add(confirmationResponse)

        val document = DocumentContract(
            id = dto.confirmationResponse.value.verification.first().value,
            documentType = DocumentTypeContract.CONTRACT_SIGNED,
            relatedConfirmations = mutableListOf(confirmationResponse.id),
            title = null,
            description = null,
            relatedLots = null
        )
        val documents = contractProcess.contract.documents?.toMutableList() ?: mutableListOf()
        documents.add(document)

        contractProcess.contract.milestones?.asSequence()
                ?.filter { it.subtype == MilestoneSubType.SUPPLIERS_APPROVAL }
                ?.forEach { milestone ->
                    milestone.apply {
                        dateModified = startDate
                        dateMet = confirmationResponse.value.date
                        status = MilestoneStatus.MET
                    }
                }

        var treasuryValidation = false
        val treasuryBudgetSourcesRs = ArrayList<TreasuryBudgetSourceSupplierSigning>()
        val confirmationRequests = contractProcess.contract.confirmationRequests ?: mutableListOf()
        if (isApproveBodyValidationPresent(contractProcess.contract.milestones)) {
            val confirmationRequest = generateApproveBodyConfirmationRequest(confirmationResponse

            )
            confirmationRequests.add(confirmationRequest)
            treasuryValidation = true
            val treasuryBudgetSources = contractProcess.treasuryBudgetSources
                    ?: throw ErrorException(TREASURY_BUDGET_SOURCES)
            val budgetAllocation = contractProcess.planning?.budget?.budgetAllocation
                    ?: throw ErrorException(BUDGET_ALLOCATION)

            for (treasuryBudgetSource in treasuryBudgetSources) {
                val totalAmount = budgetAllocation.asSequence()
                        .filter { it.budgetBreakdownID == treasuryBudgetSource.budgetBreakdownID }
                        .sumByDouble { it.amount.toDouble() }
                        .toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                treasuryBudgetSourcesRs.add(TreasuryBudgetSourceSupplierSigning(
                        budgetBreakdownID = treasuryBudgetSource.budgetBreakdownID,
                        budgetIBAN = treasuryBudgetSource.budgetIBAN,
                        amount = totalAmount))
            }
        }

        contractProcess.contract.confirmationRequests = confirmationRequests
        contractProcess.contract.confirmationResponses = confirmationResponses
        contractProcess.contract.documents = documents
        contractProcess.contract.statusDetails = ContractStatusDetails.SIGNED

        entity.statusDetails = ContractStatusDetails.SIGNED
        entity.jsonData = toJson(contractProcess)
        acDao.save(entity)
        return SupplierSigningRs(treasuryValidation, treasuryBudgetSourcesRs, contractProcess.contract)
    }

    private fun isApproveBodyValidationPresent(milestones: List<Milestone>?): Boolean {
        return milestones?.asSequence()?.any { it.subtype == MilestoneSubType.APPROVE_BODY_VALIDATION } ?: false
    }

    private fun validateRelatedPersonId(contractProcess: ContractProcess, dto: ProceedResponseRq, requestId: String) {
        var isRelatedPersonIdPresent = false
        contractProcess.contract.confirmationRequests?.forEach { confirmationRequest ->
            confirmationRequest.requestGroups?.forEach { requestGroup ->
                requestGroup.requests.forEach { request ->
                    if (request.id == requestId) {
                        if (request.relatedPerson != null) {
                            if (request.relatedPerson.id == dto.confirmationResponse.value.relatedPerson.id) {
                                isRelatedPersonIdPresent = true
                            }
                        }
                    }
                }
            }
        }
        if (!isRelatedPersonIdPresent) throw ErrorException(INVALID_RELATED_PERSON_ID)
    }

    private fun generateBuyerConfirmationResponse(buyer: OrganizationReferenceBuyer, dto: ConfirmationResponseRq, country: String, pmd: String, language: String, relatedPerson: RelatedPerson, requestId: String): ConfirmationResponse {
        val templateBuyer = templateService.getConfirmationRequestTemplate(
                country = country,
                pmd = pmd,
                language = language,
                templateId = "cs-buyer-confirmation-on")

        val templateRationale = templateService.getVerificationTemplate(
                country = country,
                pmd = pmd,
                language = language,
                templateId = "verification_rationale"
        )

        val verification = Verification(
            type = ConfirmationResponseType.DOCUMENT,
            value = dto.value.verification.first().value,
            rationale = templateRationale
        )

        val buyerName = buyer.name ?: throw ErrorException(BUYER_NAME_IS_EMPTY)

        val confirmationResponseValue = ConfirmationResponseValue(
                name = buyerName,
                id = dto.value.id,
                date = dto.value.date,
                relatedPerson = relatedPerson,
                verification = listOf(verification)

        )
        return ConfirmationResponse(
                id = templateBuyer.id
                        + dto.value.verification.firstOrNull()?.value
                        + "-"
                        + dto.value.relatedPerson.id,
                value = confirmationResponseValue,
                request = requestId
        )
    }

    private fun generateSupplierConfirmationResponse(supplier: OrganizationReferenceSupplier,
                                                     dto: ConfirmationResponseRq,
                                                     country: String,
                                                     pmd: String,
                                                     language: String,
                                                     relatedPerson: RelatedPerson,
                                                     requestId: String): ConfirmationResponse {
        val templateBuyer = templateService.getConfirmationRequestTemplate(
                country = country,
                pmd = pmd,
                language = language,
                templateId = "cs-tenderer-confirmation-on")

        val templateRationale = templateService.getVerificationTemplate(
                country = country,
                pmd = pmd,
                language = language,
                templateId = "verification_rationale"
        )

        val verification = Verification(
                type = ConfirmationResponseType.DOCUMENT,
                value = dto.value.verification.first().value,
                rationale = templateRationale
        )

        val supplierName = supplier.name

        val confirmationResponseValue = ConfirmationResponseValue(
                name = supplierName,
                id = dto.value.id,
                date = dto.value.date,
                relatedPerson = relatedPerson,
                verification = listOf(verification)

        )
        return ConfirmationResponse(
                id = templateBuyer.id
                        + dto.value.verification.firstOrNull()?.value
                        + "-"
                        + dto.value.relatedPerson.id,
                value = confirmationResponseValue,
                request = requestId
        )
    }

    private fun getAuthorityOrganizationPerson(contractProcess: ContractProcess, requestId: String): RelatedPerson {
        var relatedPerson: RelatedPerson? = null
        contractProcess.contract.confirmationRequests?.forEach { confirmationRequest ->
            confirmationRequest.requestGroups?.forEach { requestGroup ->
                requestGroup.requests.forEach { request ->
                    if (request.id == requestId) {
                        relatedPerson = request.relatedPerson
                    }
                }
            }
        }
        return relatedPerson ?: throw ErrorException(INVALID_RELATED_PERSON_ID)
    }

    private fun generateSupplierConfirmationRequest(supplier: OrganizationReferenceSupplier,
                                                    country: String,
                                                    pmd: String,
                                                    language: String,
                                                    verificationValue: String): ConfirmationRequest {
        val template = templateService.getConfirmationRequestTemplate(
                country = country,
                pmd = pmd,
                language = language,
                templateId = "cs-tenderer-confirmation-on")
        val relatedPerson = getAuthorityOrganizationPersonSupplierForBuyerStep(supplier)

        val request = Request(
                id = template.id + verificationValue + "-" + relatedPerson.id,
                title = template.requestTitle + relatedPerson.name,
                description = template.description,
                relatedPerson = relatedPerson
        )
        val requestGroup = RequestGroup(
                id = template.id + verificationValue + "-" + supplier.identifier.id,
                requests = listOf(request)
        )
        return ConfirmationRequest(
            id = template.id + verificationValue,
            relatedItem = verificationValue,
            source = ConfirmationRequestSource.TENDERER,
            type = template.type,
            title = template.title,
            description = template.description,
            relatesTo = template.relatesTo,
            requestGroups = listOf(requestGroup))
    }

    private fun generateApproveBodyConfirmationRequest(confirmationResponse: ConfirmationResponse): ConfirmationRequest {

        val relatedItem = confirmationResponse.value.verification.first().value

        val request = Request(
                id = "cs-approveBody-confirmation-on-$relatedItem-approveBodyID",
                title = "TEST",
                description = "TEST",
                relatedPerson = null
        )
        val requestGroup = RequestGroup(
                id = "TEST",
                requests = listOf(request)
        )
        return ConfirmationRequest(
                id = "cs-approveBody-confirmation-on-$relatedItem",
                relatedItem = relatedItem,
                source = ConfirmationRequestSource.APPROVE_BODY,
                type = "outsideAction",
                title = "Document approving",
                description = "TEST",
                relatesTo = "document",
                requestGroups = listOf(requestGroup))
    }

    private fun getAuthorityOrganizationPersonSupplierForBuyerStep(supplier: OrganizationReferenceSupplier): RelatedPerson {
        val persones = supplier.persones
        if (persones != null && persones.isNotEmpty()) {
            for (person in persones) {
                val id: String? = person.businessFunctions.firstOrNull { it.type == "authority" }?.id
                if (id != null)
                    return RelatedPerson(id = person.identifier.id, name = person.name)
            }
        }
        throw ErrorException(BUYER_NAME_IS_EMPTY)
    }

}
