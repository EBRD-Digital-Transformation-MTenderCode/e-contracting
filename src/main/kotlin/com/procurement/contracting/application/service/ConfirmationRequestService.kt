package com.procurement.contracting.application.service

import com.procurement.contracting.application.repository.ac.AwardContractRepository
import com.procurement.contracting.application.repository.can.CANRepository
import com.procurement.contracting.application.repository.confirmation.ConfirmationRequestEntity
import com.procurement.contracting.application.repository.confirmation.ConfirmationRequestRepository
import com.procurement.contracting.application.repository.fc.FrameworkContractRepository
import com.procurement.contracting.application.repository.pac.PacRepository
import com.procurement.contracting.application.service.errors.CreateConfirmationRequestsErrors
import com.procurement.contracting.application.service.errors.GetRequestByConfirmationResponseErrors
import com.procurement.contracting.application.service.model.CreateConfirmationRequestsParams
import com.procurement.contracting.application.service.model.GetRequestByConfirmationResponseParams
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.ac.id.AwardContractId
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequest
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestId
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestReleaseTo
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestSource
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestType
import com.procurement.contracting.domain.model.fc.id.FrameworkContractId
import com.procurement.contracting.domain.model.organization.OrganizationRole
import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.domain.model.process.Stage
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.v2.model.response.CreateConfirmationRequestsResponse
import com.procurement.contracting.infrastructure.handler.v2.model.response.GetRequestByConfirmationResponseResponse
import com.procurement.contracting.infrastructure.handler.v2.model.response.fromDomain
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.ValidationResult
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess
import com.procurement.contracting.lib.functional.asValidationError
import org.springframework.stereotype.Service

interface ConfirmationRequestService {
    fun create(params: CreateConfirmationRequestsParams): Result<CreateConfirmationRequestsResponse, Fail>
    fun get(params: GetRequestByConfirmationResponseParams): Result<GetRequestByConfirmationResponseResponse, Fail>
}

@Service
class ConfirmationRequestServiceImpl(
    private val generationService: GenerationService,
    private val transform: Transform,
    private val acRepository: AwardContractRepository,
    private val fcRepository: FrameworkContractRepository,
    private val canRepository: CANRepository,
    private val pacRepository: PacRepository,
    private val confirmationRequestRepository: ConfirmationRequestRepository,
) : ConfirmationRequestService {


    override fun create(params: CreateConfirmationRequestsParams): Result<CreateConfirmationRequestsResponse, Fail> {
        val receivedContract = params.contracts.first()
        checkContractExists(params.cpid, params.ocid, receivedContract).doOnError { return it.asFailure() }
        checkContractDocuments(receivedContract).doOnError { return it.asFailure() }

        val receivedOrganizations = getOrganizationsByRole(params).onFailure { return it }

        // FR.COM-6.12.1
        val confirmationRequest = ConfirmationRequest(
            id = ConfirmationRequestId.generate(), // FR.COM-6.12.2
            type = ConfirmationRequestType.DIGITAL_SIGNATURE, // FR.COM-6.12.3
            relatesTo = defineRelation(receivedContract.documents), // FR.COM-6.12.4
            relatedItem = defineRelatedItem(receivedContract), // FR.COM-6.12.5
            source = defineSource(params.role), // FR.COM-6.12.6
            requests = receivedOrganizations
                .map {
                    ConfirmationRequest.Request(
                        id = generationService.getTimeBasedUUID(),  // FR.COM-6.12.7
                        relatedOrganization  = ConfirmationRequest.Request.Organization(
                            id = it.id,    // FR.COM-6.12.8
                            name = it.name // FR.COM-6.12.9
                        ),
                        owner = it.owner,  // FR.COM-6.12.10
                        token = Token.generate() // FR.COM-6.12.11
                    )
                }
        )

        val confirmationRequestEntity = ConfirmationRequestEntity.of(params.cpid, params.ocid, receivedContract.id, confirmationRequest, transform)
            .onFailure { return it }

        val wasApplied = confirmationRequestRepository.save(entity = confirmationRequestEntity).onFailure { return it }
        if (!wasApplied)
            return Fail.Incident.Database.ConsistencyIncident("Record already exists.").asFailure()

        return CreateConfirmationRequestsResponse.Contract.ConfirmationRequest.fromDomain(confirmationRequest)
            .let { CreateConfirmationRequestsResponse.Contract(id = receivedContract.id, confirmationRequests = listOf(it)) }
            .let { CreateConfirmationRequestsResponse(contracts = listOf(it)) }
            .asSuccess()
    }

    override fun get(params: GetRequestByConfirmationResponseParams): Result<GetRequestByConfirmationResponseResponse, Fail> {
        val receivedContract = params.contracts.first()
        val receivedConfirmationResponse = receivedContract.confirmationResponses.first()
        val targetRequestId = receivedConfirmationResponse.requestId.toString()

        checkContractExists(params.cpid, params.ocid, receivedContract).doOnError { return it.asFailure() }

        val targetConfirmationRequest = confirmationRequestRepository
            .findBy(params.cpid, params.ocid).onFailure { return it }
            .find { confirmationRequest -> confirmationRequest.requests.any { it == targetRequestId } }
            ?.let { transform.tryDeserialization(it.jsonData, ConfirmationRequest::class.java).onFailure { return it } }
            ?: return GetRequestByConfirmationResponseErrors.ConfirmationRequestNotFound(params.cpid, params.ocid, targetRequestId).asFailure()

        return GetRequestByConfirmationResponseResponse.Contract.ConfirmationRequest.fromDomain(targetConfirmationRequest)
            .let { GetRequestByConfirmationResponseResponse.Contract(id = receivedContract.id, confirmationRequests = listOf(it)) }
            .let { GetRequestByConfirmationResponseResponse(contracts = listOf(it)) }
            .asSuccess()
    }

    private fun checkContractExists(
        cpid: Cpid, ocid: Ocid,
        receivedContract: GetRequestByConfirmationResponseParams.Contract
    ): ValidationResult<Fail> {
        val contractId = receivedContract.id

        when (ocid.stage) {
            Stage.FE -> fcRepository
                .findBy(cpid, ocid, FrameworkContractId.orNull(contractId)!!).onFailure { return it.reason.asValidationError() }
                ?: return GetRequestByConfirmationResponseErrors.ContractNotFound(cpid, ocid, contractId).asValidationError()

            Stage.EV,
            Stage.TP,
            Stage.NP -> canRepository
                .findBy(cpid, CANId.orNull(contractId)!!).onFailure { return it.reason.asValidationError() }
                ?: return GetRequestByConfirmationResponseErrors.ContractNotFound(cpid, ocid, contractId).asValidationError()

            Stage.AC -> acRepository
                .findBy(cpid, AwardContractId.orNull(contractId)!!).onFailure { return it.reason.asValidationError() }
                ?: return GetRequestByConfirmationResponseErrors.ContractNotFound(cpid, ocid, contractId).asValidationError()

            Stage.PC -> pacRepository
                .findBy(cpid, ocid, PacId.orNull(contractId)!!).onFailure { return it.reason.asValidationError() }
                ?: return GetRequestByConfirmationResponseErrors.ContractNotFound(cpid, ocid, contractId).asValidationError()

            Stage.EI,
            Stage.FS,
            Stage.PN,
            Stage.RQ -> return GetRequestByConfirmationResponseErrors.InvalidStage(ocid.stage).asValidationError()
        }

        return ValidationResult.ok()
    }

    private fun checkContractExists(
        cpid: Cpid, ocid: Ocid,
        receivedContract: CreateConfirmationRequestsParams.Contract
    ): ValidationResult<Fail> {
        val contractId = receivedContract.id

        when (ocid.stage) {
            Stage.FE -> fcRepository
                .findBy(cpid, ocid, FrameworkContractId.orNull(contractId)!!).onFailure { return it.reason.asValidationError() }
                ?: return CreateConfirmationRequestsErrors.ContractNotFound(cpid, ocid, contractId).asValidationError()

            Stage.EV,
            Stage.TP,
            Stage.NP -> canRepository
                .findBy(cpid, CANId.orNull(contractId)!!).onFailure { return it.reason.asValidationError() }
                ?: return CreateConfirmationRequestsErrors.ContractNotFound(cpid, ocid, contractId).asValidationError()

            Stage.AC -> acRepository
                .findBy(cpid, AwardContractId.orNull(contractId)!!).onFailure { return it.reason.asValidationError() }
                ?: return CreateConfirmationRequestsErrors.ContractNotFound(cpid, ocid, contractId).asValidationError()

            Stage.PC -> pacRepository
                .findBy(cpid, ocid, PacId.orNull(contractId)!!).onFailure { return it.reason.asValidationError() }
                ?: return CreateConfirmationRequestsErrors.ContractNotFound(cpid, ocid, contractId).asValidationError()

            Stage.EI,
            Stage.FS,
            Stage.PN,
            Stage.RQ -> return CreateConfirmationRequestsErrors.InvalidStage(ocid.stage).asValidationError()
        }

        return ValidationResult.ok()
    }


    private fun checkContractDocuments(receivedContract: CreateConfirmationRequestsParams.Contract): ValidationResult<CreateConfirmationRequestsErrors> =
        if (receivedContract.documents.size > 1)
            CreateConfirmationRequestsErrors.TooMachDocuments().asValidationError()
        else
            ValidationResult.ok()

    private fun defineRelation(documents: List<CreateConfirmationRequestsParams.Contract.Document>): ConfirmationRequestReleaseTo =
        if (documents.isNotEmpty())
            ConfirmationRequestReleaseTo.DOCUMENT
        else
            ConfirmationRequestReleaseTo.CONTRACT

    private fun defineRelatedItem(receivedContract: CreateConfirmationRequestsParams.Contract): String =
        if (receivedContract.documents.isNotEmpty())
            receivedContract.documents.first().id
        else
            receivedContract.id

    private fun defineSource(role: OrganizationRole): ConfirmationRequestSource =
        when (role) {
            OrganizationRole.BUYER -> ConfirmationRequestSource.BUYER
            OrganizationRole.SUPPLIER -> ConfirmationRequestSource.TENDERER
            OrganizationRole.PROCURING_ENTITY -> ConfirmationRequestSource.PROCURING_ENTITY
            OrganizationRole.INVITED_CANDIDATE -> ConfirmationRequestSource.INVITED_CANDIDATE
        }

    private fun getOrganizationsByRole(params: CreateConfirmationRequestsParams): Result<List<Organization>, Fail> =
        when (params.role) {
            OrganizationRole.BUYER -> params.access?.buyers
                ?.map { Organization(id = it.id, name = it.name, owner = it.owner) }
                ?.takeIf { it.isNotEmpty() }
                ?.asSuccess()
                ?: CreateConfirmationRequestsErrors.AttributeNotFound("access.buyers").asFailure()

            OrganizationRole.SUPPLIER -> params.submission?.tenderers
                ?.flatMap { tenderer ->
                    tenderer.organizations
                        .map { Organization(id = it.id, name = it.name, owner = tenderer.owner)  }
                }
                ?.takeIf { it.isNotEmpty() }
                ?.asSuccess()
                ?: CreateConfirmationRequestsErrors.AttributeNotFound("submission.tenderers").asFailure()

            OrganizationRole.PROCURING_ENTITY -> params.access?.procuringEntity
                ?.let { listOf(Organization(id = it.id, name = it.name, owner = it.owner)) }
                ?.asSuccess()
                ?: CreateConfirmationRequestsErrors.AttributeNotFound("access.procuringEntity").asFailure()

            OrganizationRole.INVITED_CANDIDATE -> params.dossier?.candidates
                ?.flatMap { candidate ->
                    candidate.organizations
                        .map { Organization(id = it.id, name = it.name, owner = candidate.owner) }
                }
                ?.takeIf { it.isNotEmpty() }
                ?.asSuccess()
                ?: CreateConfirmationRequestsErrors.AttributeNotFound("dossier.candidates").asFailure()
        }

    private data class Organization(val id: String, val name: String, val owner: String)
}
