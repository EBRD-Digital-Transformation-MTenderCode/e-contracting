package com.procurement.contracting.application.service.rule.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

class ValidContractStatesRule(states: List<State>) : List<ValidContractStatesRule.State> by states {

    data class State(
        @field:JsonProperty("status") @param:JsonProperty("status") val status: Status,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: StatusDetails
    ) {
        data class Status(
            val value: String
        )

        data class StatusDetails(
            val value: String?
        )

        fun matches(expected: State) =
            if (expected.statusDetails.value == null)
                status.value == expected.status.value
            else status.value == expected.status.value
                && statusDetails.value == expected.statusDetails.value
    }
}
