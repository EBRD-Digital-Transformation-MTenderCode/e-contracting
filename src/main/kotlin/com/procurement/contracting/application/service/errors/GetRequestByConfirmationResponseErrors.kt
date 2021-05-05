package com.procurement.contracting.application.service.errors

import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.domain.model.process.Stage
import com.procurement.contracting.infrastructure.fail.error.ValidationCommandError

sealed class GetRequestByConfirmationResponseErrors(
    override val description: String,
    numberError: String,
) : ValidationCommandError() {

    override val code: String = prefix + numberError

    class InvalidStage(stage: Stage) : GetRequestByConfirmationResponseErrors(
        numberError = "6.15.1",
        description = "Invalid stage '$stage'."
    )

    class ContractNotFound(cpid: Cpid, ocid: Ocid, contractId: String) : GetRequestByConfirmationResponseErrors(
        numberError = "6.15.2",
        description = "Contract not found by cpid '$cpid', ocid '$ocid' and contract id '$contractId'."
    )

    class ConfirmationRequestNotFound(cpid: Cpid, ocid: Ocid, requestId: String) : GetRequestByConfirmationResponseErrors(
        numberError = "6.15.3",
        description = "Contract not found by cpid '$cpid', ocid '$ocid' and requestId '$requestId'."
    )
}
