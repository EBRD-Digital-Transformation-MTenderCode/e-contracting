package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.exception.ErrorType.*
import com.procurement.contracting.model.dto.*
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.model.dto.ocds.*
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toLocalDateTime
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class SigningAcService(private val acDao: AcDao,
                       private val generationService: GenerationService,
                       private val templateService: TemplateService) {

    fun buyerSigningAC(cm: CommandMessage): ResponseDto {
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
        if (entity.owner != owner) throw ErrorException(ErrorType.OWNER)
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
        val supplier = contractProcess.award.suppliers.first()
        val confirmationRequest = generateSupplierConfirmationRequest(
                supplier = supplier,
                country = country,
                pmd = pmd,
                language = language,
                verificationValue = dto.confirmationResponse.value.verification.first().value
        )
        val confirmationRequests = contractProcess.contract.confirmationRequests?.toMutableList() ?: mutableListOf()
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
        contractProcess.contract.confirmationResponses = mutableListOf(confirmationResponse)
        contractProcess.contract.documents = documents

        entity.statusDetails=ContractStatusDetails.APPROVED.toString()
        entity.jsonData = toJson(contractProcess)
        acDao.save(entity)
        return ResponseDto(data = BuyerSigningRs(contractProcess.contract))
    }

    fun supplierSigningAC(cm: CommandMessage): ResponseDto {
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

        if (contractProcess.contract.confirmationResponses?.firstOrNull()?.value?.id != requestId) throw ErrorException(INVALID_REQUEST_ID)
        if (dto.confirmationResponse.value.id != contractProcess.award.suppliers.firstOrNull()?.id) throw ErrorException(INVALID_SUPPLIER_ID)
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
        val confirmationResponses = contractProcess.contract.confirmationResponses?.toMutableList() ?: mutableListOf()
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
        val treasuryBudgetSources = ArrayList<TreasuryBudgetSourceSupplierSigning>()
        val confirmationRequests = contractProcess.contract.confirmationRequests?.toMutableList() ?: mutableListOf()

        if (isApproveBodyValidationPresent(contractProcess.contract.milestones)) {
            val confirmationRequest = generateApproveBodyConfirmationRequest()
            confirmationRequests.add(confirmationRequest)
            treasuryValidation = true
            treasuryBudgetSources.add(TreasuryBudgetSourceSupplierSigning(
                    budgetBreakdownID = "hardCode!",
                    budgetIBAN = "hardCode!",
                    amount = BigDecimal(0)
            ))
        }

        contractProcess.contract.confirmationRequests = confirmationRequests
        contractProcess.contract.confirmationResponses = confirmationResponses
        contractProcess.contract.documents = documents
        contractProcess.contract.statusDetails = ContractStatusDetails.SIGNED

        entity.statusDetails=ContractStatusDetails.SIGNED.toString()
        entity.jsonData = toJson(contractProcess)
        acDao.save(entity)
        return ResponseDto(data = SupplierSigningRs(treasuryValidation, treasuryBudgetSources, contractProcess.contract))
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

        val templateRationale = templateService.getConfirmationRequestTemplate(
                country = country,
                pmd = pmd,
                language = language,
                templateId = "verification_rationale"
        )

        val verification = Verification(
                type = ConfirmationResponseType.DOCUMENT,
                value = dto.value.verification.first().value,
                rationale = templateRationale.description!!
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

    private fun generateSupplierConfirmationResponse(supplier: OrganizationReferenceSupplier, dto: ConfirmationResponseRq, country: String, pmd: String, language: String, relatedPerson: RelatedPerson, requestId: String): ConfirmationResponse {
        val templateBuyer = templateService.getConfirmationRequestTemplate(
                country = country,
                pmd = pmd,
                language = language,
                templateId = "cs-tenderer-confirmation-on")

        val templateRationale = templateService.getConfirmationRequestTemplate(
                country = country,
                pmd = pmd,
                language = language,
                templateId = "verification_rationale"
        )

        val verification = Verification(
                type = ConfirmationResponseType.DOCUMENT,
                value = dto.value.verification.first().value,
                rationale = templateRationale.description!!
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
                description = template.description!!,
                relatedPerson = relatedPerson
        )
        val requestGroup = RequestGroup(
                id = template.id + verificationValue + "-" + supplier.identifier.id,
                requests = hashSetOf(request)
        )
        return ConfirmationRequest(
                id = template.id + verificationValue,
                relatedItem = verificationValue,
                source = template.source ?: "",
                type = template.type,
                title = template.title,
                description = template.description,
                relatesTo = template.relatesTo,
                requestGroups = hashSetOf(requestGroup))
    }

    private fun generateApproveBodyConfirmationRequest(): ConfirmationRequest {

        val relatedPerson = RelatedPerson(
                id = "hardCode!",
                name = "hardCode!"
        )

        val request = Request(
                id = "hardCode!",
                title = "hardCode!",
                description = "hardCode!",
                relatedPerson = relatedPerson
        )
        val requestGroup = RequestGroup(
                id = "hardCode!",
                requests = hashSetOf(request)
        )
        return ConfirmationRequest(
                id = "hardCode!",
                relatedItem = "hardCode!",
                source = "hardCode!",
                type = "hardCode!",
                title = "hardCode!",
                description = "hardCode!",
                relatesTo = "hardCode!",
                requestGroups = hashSetOf(requestGroup))
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
        throw ErrorException(ErrorType.BUYER_NAME_IS_EMPTY)
    }

}
