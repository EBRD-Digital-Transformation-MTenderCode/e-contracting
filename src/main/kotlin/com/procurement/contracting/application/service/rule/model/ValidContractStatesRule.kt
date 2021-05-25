package com.procurement.contracting.application.service.rule.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

class ValidContractStatesRule(states: List<State>) : List<ValidContractStatesRule.State> by states {

    data class State(
        @field:JsonProperty("status") @param:JsonProperty("status") val status: String,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String?
    ) {
        fun matches(expected: State) =
            if (expected.statusDetails == null)
                status == expected.status
            else status == expected.status
                && statusDetails == expected.statusDetails
    }
}
