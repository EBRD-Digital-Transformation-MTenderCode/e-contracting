package com.procurement.contracting.application.service.errors

import com.procurement.contracting.application.service.rule.model.ValidContractStatesRule.State
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.domain.model.process.Stage
import com.procurement.contracting.infrastructure.fail.error.ValidationCommandError

sealed class CheckContractStateErrors(
    override val description: String,
    numberError: String,
) : ValidationCommandError() {

    override val code: String = prefix + numberError

    class InvalidContractState(id: String, currentStatus: String, currentStatusDetails: String?, validStates: List<State>) : CheckContractStateErrors(
        numberError = "6.8.2",
        description = "Contract '$id' has invalid state (status: '${currentStatus}' and statusDetails: '${currentStatusDetails}')." +
            "Valid states: $validStates."
    )

    class ContractNotFound(cpid: Cpid, ocid: Ocid, ids: List<String>) : CheckContractStateErrors(
        numberError = "6.8.1",
        description = "Contract(s) not found by cpid '$cpid', ocid '$ocid' and contract id(s) '${ids.joinToString()}'."
    )

    class InvalidStage(stage: Stage) : CheckContractStateErrors(
        numberError = "6.8.3",
        description = "Stage '$stage' is invalid"
    )

    class InvalidContractId(id: String, pattern: String) : CheckContractStateErrors(
        numberError = "6.8.4",
        description = "Invalid contract id '$id'. Mismatch to pattern '$pattern'."
    )

}
