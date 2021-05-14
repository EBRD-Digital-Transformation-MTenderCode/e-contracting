package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.CreateConfirmationRequestsParams
import com.procurement.contracting.domain.util.extension.mapResult
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.infrastructure.handler.v2.model.request.CreateConfirmationRequestsRequest
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess

fun CreateConfirmationRequestsRequest.convert(): Result<CreateConfirmationRequestsParams, DataErrors.Validation> {
    val convertedContracts = contracts.mapResult { it.convert() }.onFailure { return it }
    val convertedAccess = access?.convert()?.onFailure { return it }
    val convertedDossier = dossier?.convert()?.onFailure { return it }
    val convertedSubmission = submission?.convert()?.onFailure { return it }

    return CreateConfirmationRequestsParams.tryCreate(
        cpid = cpid,
        ocid = ocid,
        role = role,
        contracts = convertedContracts,
        access = convertedAccess,
        dossier = convertedDossier,
        submission = convertedSubmission
    )
}

fun CreateConfirmationRequestsRequest.Contract.convert(): Result<CreateConfirmationRequestsParams.Contract, DataErrors.Validation> {
    val convertedDocuments = documents.orEmpty().map { it.convert() }
    return CreateConfirmationRequestsParams.Contract
        .tryCreate(id = id, documents = convertedDocuments).onFailure { return it }
        .asSuccess()
}

fun CreateConfirmationRequestsRequest.Contract.Document.convert() =
    CreateConfirmationRequestsParams.Contract.Document(id = id)

fun CreateConfirmationRequestsRequest.Access.convert(): Result<CreateConfirmationRequestsParams.Access, DataErrors.Validation.UniquenessDataMismatch> {
    val convertedBuyers = buyers.orEmpty().map { it.convert() }
    val convertedProcuringEntity = procuringEntity?.convert()
    return CreateConfirmationRequestsParams.Access.tryCreate(convertedBuyers, convertedProcuringEntity)
}

fun CreateConfirmationRequestsRequest.Access.Buyer.convert() =
    CreateConfirmationRequestsParams.Access.Buyer(id = id, name = name, owner = owner)

fun CreateConfirmationRequestsRequest.Access.ProcuringEntity.convert() =
    CreateConfirmationRequestsParams.Access.ProcuringEntity(id = id, name = name, owner = owner)

fun CreateConfirmationRequestsRequest.Dossier.convert(): Result<CreateConfirmationRequestsParams.Dossier, DataErrors.Validation.UniquenessDataMismatch> {
    val convertedCandidates = candidates.mapResult { it.convert() }.onFailure { return it }
    return CreateConfirmationRequestsParams.Dossier(convertedCandidates).asSuccess()
}

fun CreateConfirmationRequestsRequest.Dossier.Candidate.convert(): Result<CreateConfirmationRequestsParams.Dossier.Candidate, DataErrors.Validation.UniquenessDataMismatch> {
    val convertedOrganizations = organizations.map { it.convert() }
    return CreateConfirmationRequestsParams.Dossier.Candidate.tryCreate(owner = owner, organizations = convertedOrganizations)
}

fun CreateConfirmationRequestsRequest.Dossier.Candidate.Organization.convert() =
    CreateConfirmationRequestsParams.Dossier.Candidate.Organization(id = id, name = name)

fun CreateConfirmationRequestsRequest.Submission.convert(): Result<CreateConfirmationRequestsParams.Submission, DataErrors.Validation.UniquenessDataMismatch> {
    val convertedTenderers = tenderers.mapResult { it.convert() }.onFailure { return it }
    return CreateConfirmationRequestsParams.Submission(convertedTenderers).asSuccess()
}

fun CreateConfirmationRequestsRequest.Submission.Tenderer.convert(): Result<CreateConfirmationRequestsParams.Submission.Tenderer, DataErrors.Validation.UniquenessDataMismatch> {
    val convertedOrganizations = organizations.map { it.convert() }
    return CreateConfirmationRequestsParams.Submission.Tenderer.tryCreate(owner = owner, organizations = convertedOrganizations)
}

fun CreateConfirmationRequestsRequest.Submission.Tenderer.Organization.convert() =
    CreateConfirmationRequestsParams.Submission.Tenderer.Organization(id = id, name = name)