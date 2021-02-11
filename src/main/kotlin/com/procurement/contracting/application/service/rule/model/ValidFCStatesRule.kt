package com.procurement.contracting.application.service.rule.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatus
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatusDetails

class ValidFCStatesRule(states: List<State>) : List<ValidFCStatesRule.State> by states {

    data class State(
        @field:JsonProperty("status") @param:JsonProperty("status") val status: FrameworkContractStatus,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: FrameworkContractStatusDetails
    )
}
