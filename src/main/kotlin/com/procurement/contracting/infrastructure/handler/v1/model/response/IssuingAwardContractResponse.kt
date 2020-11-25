package com.procurement.contracting.infrastructure.handler.v1.model.response


import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.ac.status.AwardContractStatusDetails
import java.time.LocalDateTime

data class IssuingAwardContractResponse(
    @field:JsonProperty("contract") @param:JsonProperty("contract") val contract: AwardContract
) {
    data class AwardContract(
        @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: AwardContractStatusDetails
    )
}