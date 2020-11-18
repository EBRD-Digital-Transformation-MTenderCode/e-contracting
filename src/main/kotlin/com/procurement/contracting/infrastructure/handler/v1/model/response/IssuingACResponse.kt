package com.procurement.contracting.infrastructure.handler.v1.model.response


import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails
import java.time.LocalDateTime

data class IssuingACResponse(
    @field:JsonProperty("contract") @param:JsonProperty("contract") val contract: Contract
) {
    data class Contract(
        @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: ContractStatusDetails
    )
}