package com.procurement.contracting.application.service.errors

import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.domain.model.process.Stage
import com.procurement.contracting.infrastructure.fail.error.ValidationCommandError

sealed class CheckAccessToContractErrors(
    override val description: String,
    numberError: String,
) : ValidationCommandError() {

    override val code: String = prefix + numberError

    class UnexpectedStage(stage: Stage) : CheckAccessToContractErrors(
        numberError = "6.17.1",
        description = "Unexpected stage '$stage'."
    )

    class ContractNotFound(cpid: Cpid, ocid: Ocid, contractId: String) : CheckAccessToContractErrors(
        numberError = "6.17.2",
        description = "Contract not found by cpid '$cpid', ocid '$ocid' and contract id '$contractId'."
    )

    class InvalidContractId(id: String, pattern: String) : CheckAccessToContractErrors(
        numberError = "6.17.3",
        description = "Invalid contract id '$id'. Mismatch to pattern '$pattern'."
    )

    class TokenMismatch() : CheckAccessToContractErrors(
        numberError = "6.11.4",
        description = "Received token does not match stored one."
    )

    class OwnerMismatch() : CheckAccessToContractErrors(
        numberError = "6.11.5",
        description = "Received owner does not match stored one."
    )
}
