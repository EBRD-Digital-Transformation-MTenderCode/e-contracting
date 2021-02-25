package com.procurement.contracting.application.service.errors

import com.procurement.contracting.application.service.rule.model.ValidFCStatesRule.State
import com.procurement.contracting.domain.model.fc.id.FrameworkContractId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.error.ValidationCommandError

sealed class CheckContractStateErrors(
    override val description: String,
    numberError: String,
) : ValidationCommandError() {

    override val code: String = prefix + numberError

    class InvalidContractState(currentState: State, validStates: List<State>) : CheckContractStateErrors(
        numberError = "6.8.2",
        description = "Contract has invalid state (status: '${currentState.status}' and statusDetails: '${currentState.statusDetails}')." +
            "Valid states: $validStates."
    )

    class ContractNotFound(cpid: Cpid, ocid: Ocid, id: FrameworkContractId) : CheckContractStateErrors(
        numberError = "6.8.1",
        description = "Contract not found by cpid '$cpid', ocid '$ocid' and contract id '$id'."
    )
}
