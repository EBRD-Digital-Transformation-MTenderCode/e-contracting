package com.procurement.contracting.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateFrameworkContractResponse(

    @field:JsonProperty("token") @param:JsonProperty("token") val token: String,
    @field:JsonProperty("contracts") @param:JsonProperty("contracts") val contracts: List<Contract>,
) {
    data class Contract(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("status") @param:JsonProperty("status") val status: String,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String,
        @field:JsonProperty("date") @param:JsonProperty("date") val date: String,
        @get:JsonProperty("isFrameworkOrDynamic") @param:JsonProperty("isFrameworkOrDynamic") val isFrameworkOrDynamic: Boolean
    )
}
