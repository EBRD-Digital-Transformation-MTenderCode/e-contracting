package com.procurement.contracting.application.service.errors

import com.procurement.contracting.domain.model.bid.BusinessFunctionType
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.error.ValidationCommandError

sealed class ValidateConfirmationResponseDataErrors(
    override val description: String,
    numberError: String,
) : ValidationCommandError() {

    override val code: String = prefix + numberError

    class ContractNotFound(cpid: Cpid, ocid: Ocid, contractId: String) : ValidateConfirmationResponseDataErrors(
        numberError = "6.14.1",
        description = "Contract not found by cpid '${cpid.underlying}', ocid '${ocid.underlying}' and contract id '$contractId'."
    )

    class ConfirmationRequestNotFound(cpid: Cpid, ocid: Ocid, requestGroupId: String) : ValidateConfirmationResponseDataErrors(
        numberError = "6.14.2",
        description = "Confirmation request not found by cpid '${cpid.underlying}', ocid '${ocid.underlying}' and requestId '$requestGroupId'."
    )

    class BusinessFunctionIdNotUnique(duplicatedId: String) : ValidateConfirmationResponseDataErrors(
        numberError = "6.14.3",
        description = "Business functions ids must be unique. Duplicated id '$duplicatedId'."
    )

    class InvalidBusinessFunctionType(type: BusinessFunctionType) : ValidateConfirmationResponseDataErrors(
        numberError = "6.14.4",
        description = "Invalid business function type '${type.key}'."
    )

    class BusinessFunctionDocumentsIdNotUnique(duplicatedId: String) : ValidateConfirmationResponseDataErrors(
        numberError = "6.14.5",
        description = "Business function documents ids must be unique. Duplicated id '$duplicatedId'."
    )

    class ResponseAlreadyExists : ValidateConfirmationResponseDataErrors(
        numberError = "6.14.6",
        description = "Confirmation response on request already exists."
    )

}
