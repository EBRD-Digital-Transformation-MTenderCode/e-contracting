package com.procurement.contracting.domain.model.organization

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class MainEconomicActivity(
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,

    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
)
