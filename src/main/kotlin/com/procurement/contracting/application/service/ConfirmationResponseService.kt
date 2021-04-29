package com.procurement.contracting.application.service

import com.procurement.contracting.application.repository.ac.AwardContractRepository
import com.procurement.contracting.application.repository.can.CANRepository
import com.procurement.contracting.application.repository.confirmation.ConfirmationRequestRepository
import com.procurement.contracting.application.repository.fc.FrameworkContractRepository
import com.procurement.contracting.application.repository.pac.PacRepository
import com.procurement.contracting.application.service.errors.ValidateConfirmationResponseDataErrors
import com.procurement.contracting.application.service.model.ValidateConfirmationResponseDataParams
import com.procurement.contracting.domain.model.ac.id.AwardContractId
import com.procurement.contracting.domain.model.bid.BusinessFunctionType
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequest
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestSource
import com.procurement.contracting.domain.model.fc.id.FrameworkContractId
import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.domain.model.process.Stage
import com.procurement.contracting.domain.util.extension.getDuplicate
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.fail.error.BadRequest
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.Result.Companion.success
import com.procurement.contracting.lib.functional.ValidationResult
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asValidationError
import org.springframework.stereotype.Service

interface ConfirmationResponseService {
    fun validate(params: ValidateConfirmationResponseDataParams): ValidationResult<Fail>
}

@Service
class ConfirmationResponseServiceImpl(
    private val transform: Transform,
    private val acRepository: AwardContractRepository,
    private val fcRepository: FrameworkContractRepository,
    private val canRepository: CANRepository,
    private val pacRepository: PacRepository,
    private val confirmationRequestRepository: ConfirmationRequestRepository,
) : ConfirmationResponseService {


    override fun validate(params: ValidateConfirmationResponseDataParams): ValidationResult<Fail> {
        val receivedContract = params.contracts.first()
        val receivedConfirmationResponse = receivedContract.confirmationResponses.first()

        checkContractExists(params, receivedContract).doOnError { return it.asValidationError() }
        val storedConfirmationRequest = getConfirmationRequest(params.cpid, params.ocid, receivedConfirmationResponse)
            .onFailure { return it.reason.asValidationError()}

        checkBusinessFunction(storedConfirmationRequest, receivedConfirmationResponse).doOnError { return it.asValidationError() }

        return ValidationResult.ok()
    }

    private fun checkContractExists(
        params: ValidateConfirmationResponseDataParams,
        receivedContract: ValidateConfirmationResponseDataParams.Contract
    ): ValidationResult<Fail> {
        val cpid = params.cpid
        val ocid = params.ocid
        val contractId = receivedContract.id

        when (ocid.stage) {
            Stage.FE -> fcRepository
                .findBy(cpid, ocid, FrameworkContractId.orNull(contractId)!!).onFailure { return it.reason.asValidationError() }
                ?: return ValidateConfirmationResponseDataErrors.ContractNotFound(cpid, ocid, contractId).asValidationError()

            Stage.EV,
            Stage.TP,
            Stage.NP -> canRepository
                .findBy(cpid, CANId.orNull(contractId)!!).onFailure { return it.reason.asValidationError() }
                ?: return ValidateConfirmationResponseDataErrors.ContractNotFound(cpid, ocid, contractId).asValidationError()

            Stage.AC -> acRepository
                .findBy(cpid, AwardContractId.orNull(contractId)!!).onFailure { return it.reason.asValidationError() }
                ?: return ValidateConfirmationResponseDataErrors.ContractNotFound(cpid, ocid, contractId).asValidationError()

            Stage.PC -> pacRepository
                .findBy(cpid, ocid, PacId.orNull(contractId)!!).onFailure { return it.reason.asValidationError() }
                ?: return ValidateConfirmationResponseDataErrors.ContractNotFound(cpid, ocid, contractId).asValidationError()

            Stage.EI,
            Stage.FS,
            Stage.PN,
            Stage.RQ -> return BadRequest(description = "Invalid stage '${ocid.stage}'.", IllegalArgumentException()).asValidationError()
        }

        return ValidationResult.ok()
    }


    private fun getConfirmationRequest(
        cpid: Cpid, ocid: Ocid,
        receivedConfirmationResponse: ValidateConfirmationResponseDataParams.Contract.ConfirmationResponse
    ): Result<ConfirmationRequest, Fail> {
        val receivedRequestId = receivedConfirmationResponse.requestId.toString()

        val targetEntity = confirmationRequestRepository
            .findBy(cpid, ocid).onFailure { return it }
            .find { confirmationRequest -> confirmationRequest.requests.any { it == receivedRequestId } }
            ?: return ValidateConfirmationResponseDataErrors.ConfirmationRequestNotFound(cpid, ocid, receivedRequestId).asFailure()

        val storedConfirmationRequest = transform
            .tryDeserialization(targetEntity.jsonData, ConfirmationRequest::class.java)
            .onFailure { return it }

        return success(storedConfirmationRequest)
    }

    private fun checkBusinessFunction(
        storedConfirmationRequest: ConfirmationRequest,
        confirmationResponse: ValidateConfirmationResponseDataParams.Contract.ConfirmationResponse
    ): ValidationResult<Fail> {
        checkBusinessFunctionIdsUniqueness(confirmationResponse.relatedPerson.businessFunctions).doOnError { return it.asValidationError() }

        confirmationResponse.relatedPerson.businessFunctions
            .forEach { businessFunction ->
                checkBusinessFunctionType(storedConfirmationRequest, businessFunction).doOnError { return it.asValidationError() }
            }

        val allReceivedDocuments = confirmationResponse.relatedPerson.businessFunctions.flatMap { it.documents }
        checkDocumentsIdsUniqueness(allReceivedDocuments).doOnError { return it.asValidationError() }

        return ValidationResult.ok()
    }

    private fun checkBusinessFunctionType(confirmationRequest: ConfirmationRequest, businessFunction: ValidateConfirmationResponseDataParams.Contract.ConfirmationResponse.Person.BusinessFunction): ValidationResult<Fail> =
        when (confirmationRequest.source) {

            ConfirmationRequestSource.BUYER -> when (businessFunction.type) {
                BusinessFunctionType.CHAIRMAN,
                BusinessFunctionType.CONTACT_POINT,
                BusinessFunctionType.TECHNICAL_OPENER,
                BusinessFunctionType.PRICE_OPENER,
                BusinessFunctionType.PRICE_EVALUATOR,
                BusinessFunctionType.PROCUREMENT_OFFICER,
                BusinessFunctionType.TECHNICAL_EVALUATOR -> ValidationResult.ok()

                BusinessFunctionType.AUTHORITY -> ValidateConfirmationResponseDataErrors.InvalidBusinessFunctionType(businessFunction.type).asValidationError()
            }

            ConfirmationRequestSource.INVITED_CANDIDATE,
            ConfirmationRequestSource.TENDERER -> when (businessFunction.type) {
                BusinessFunctionType.AUTHORITY,
                BusinessFunctionType.CONTACT_POINT -> ValidationResult.ok()

                BusinessFunctionType.CHAIRMAN,
                BusinessFunctionType.TECHNICAL_OPENER,
                BusinessFunctionType.PRICE_OPENER,
                BusinessFunctionType.PRICE_EVALUATOR,
                BusinessFunctionType.PROCUREMENT_OFFICER,
                BusinessFunctionType.TECHNICAL_EVALUATOR -> ValidateConfirmationResponseDataErrors.InvalidBusinessFunctionType(businessFunction.type).asValidationError()
            }

            ConfirmationRequestSource.PROCURING_ENTITY -> when (businessFunction.type) {
                BusinessFunctionType.CHAIRMAN,
                BusinessFunctionType.CONTACT_POINT,
                BusinessFunctionType.TECHNICAL_OPENER,
                BusinessFunctionType.PRICE_OPENER,
                BusinessFunctionType.PRICE_EVALUATOR,
                BusinessFunctionType.PROCUREMENT_OFFICER,
                BusinessFunctionType.TECHNICAL_EVALUATOR -> ValidationResult.ok()

                BusinessFunctionType.AUTHORITY -> ValidateConfirmationResponseDataErrors.InvalidBusinessFunctionType(businessFunction.type).asValidationError()
            }

            ConfirmationRequestSource.APPROVE_BODY ->
                Fail.Incident.Database.ConsistencyIncident("Invalid confirmation request (id '${confirmationRequest.id}') source stored in database").asValidationError()
        }

    private fun checkBusinessFunctionIdsUniqueness(businessFunctions: List<ValidateConfirmationResponseDataParams.Contract.ConfirmationResponse.Person.BusinessFunction>): ValidationResult<Fail> {
        val duplicatedBusinessFunction = businessFunctions.getDuplicate { it.id }

        if (duplicatedBusinessFunction != null)
            return ValidateConfirmationResponseDataErrors.BusinessFunctionIdNotUnique(duplicatedBusinessFunction.id).asValidationError()
        else
            return ValidationResult.ok()
    }

    private fun checkDocumentsIdsUniqueness(documents: List<ValidateConfirmationResponseDataParams.Contract.ConfirmationResponse.Person.BusinessFunction.Document>): ValidationResult<Fail> {
        val duplicatedDocuments = documents.getDuplicate { it.id }

        if (duplicatedDocuments != null)
            return ValidateConfirmationResponseDataErrors.BusinessFunctionDocumentsIdNotUnique(duplicatedDocuments.id).asValidationError()
        else
            return ValidationResult.ok()
    }
}
