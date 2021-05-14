package com.procurement.contracting.application.service.errors

import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.domain.model.process.Stage
import com.procurement.contracting.infrastructure.fail.error.ValidationCommandError

sealed class GetContractStateErrors(
    override val description: String,
    numberError: String,
) : ValidationCommandError() {

    override val code: String = prefix + numberError

    class UnexpectedStage(stage: Stage) : GetContractStateErrors(
        numberError = "6.11.1",
        description = "Unexpected stage '$stage'."
    )

    class ContractNotFound(cpid: Cpid, ocid: Ocid, contractId: String) : GetContractStateErrors(
        numberError = "6.11.2",
        description = "Contract not found by cpid '$cpid', ocid '$ocid' and contract id '$contractId'."
    )

    class InvalidContractId(id: String, pattern: String) : GetContractStateErrors(
        numberError = "6.11.3",
        description = "Invalid contract id '$id'. Mismatch to pattern '$pattern'."
    )
}
