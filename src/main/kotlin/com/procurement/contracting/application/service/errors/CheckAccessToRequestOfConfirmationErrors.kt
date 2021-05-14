package com.procurement.contracting.application.service.errors

import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.error.ValidationCommandError

sealed class CheckAccessToRequestOfConfirmationErrors(
    override val description: String,
    numberError: String,
) : ValidationCommandError() {

    override val code: String = prefix + numberError

    class ContractNotFound(cpid: Cpid, ocid: Ocid, requestId: String) : CheckAccessToRequestOfConfirmationErrors(
        numberError = "6.13.1",
        description = "Contract not found by cpid '$cpid', ocid '$ocid' and request id '$requestId'."
    )

    class InvalidToken : CheckAccessToRequestOfConfirmationErrors(
        numberError = "6.13.2",
        description = "Token in request mismatch with stored token."
    )

    class InvalidOwner : CheckAccessToRequestOfConfirmationErrors(
        numberError = "6.13.3",
        description = "Owner in request mismatch with stored owner."
    )
}
