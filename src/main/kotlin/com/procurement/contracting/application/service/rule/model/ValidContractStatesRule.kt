package com.procurement.contracting.application.service.rule.model

import com.fasterxml.jackson.annotation.JsonProperty

class ValidContractStatesRule(states: List<State>) : List<ValidContractStatesRule.State> by states {

    data class State(
        @field:JsonProperty("status") @param:JsonProperty("status") val status: String,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String
    )
}
