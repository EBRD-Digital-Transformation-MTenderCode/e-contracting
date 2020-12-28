package com.procurement.contracting.infrastructure.handler.v1.model.request


import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class IssuingAcRequest(
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @param:JsonProperty("contract") @field:JsonProperty("contract") val contract: Contract?
) {
    data class Contract(
        @param:JsonProperty("internalId") @field:JsonProperty("internalId") val internalId: String
    )
}