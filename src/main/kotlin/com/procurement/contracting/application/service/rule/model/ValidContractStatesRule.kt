package com.procurement.contracting.application.service.rule.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatus
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatusDetails
import com.procurement.contracting.domain.model.pac.PacStatus
import com.procurement.contracting.domain.model.pac.PacStatusDetails

class ValidContractStatesRule(states: List<State>) : List<ValidContractStatesRule.State> by states {

    data class State(
        @field:JsonProperty("status") @param:JsonProperty("status") val status: Status,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: StatusDetails?
    ) {
        data class Status(
            @field:JsonProperty("value") @param:JsonProperty("value") val value: String
        )

        data class StatusDetails(
            @field:JsonProperty("value") @param:JsonProperty("value") val value: String?
        )
    }

    fun contains(status: CANStatus, statusDetails: CANStatusDetails?): Boolean =
        contains(status.key, statusDetails?.key)

    fun contains(status: PacStatus, statusDetails: PacStatusDetails?): Boolean =
        contains(status.key, statusDetails?.key)

    fun contains(status: FrameworkContractStatus, statusDetails: FrameworkContractStatusDetails?): Boolean =
        contains(status.key, statusDetails?.key)

    private fun contains(status: String, statusDetails: String?): Boolean =
        this.any { state ->
            if (state.statusDetails != null)
                status == state.status.value && statusDetails == state.statusDetails.value
            else
                state.status.value == status
        }
}
