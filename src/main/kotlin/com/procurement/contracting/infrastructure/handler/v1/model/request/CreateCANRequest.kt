package com.procurement.contracting.infrastructure.handler.v1.model.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.award.AwardId

data class CreateCANRequest(
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("award") @param:JsonProperty("award") val award: Award?
) {
    data class Award(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: AwardId
    )
}
