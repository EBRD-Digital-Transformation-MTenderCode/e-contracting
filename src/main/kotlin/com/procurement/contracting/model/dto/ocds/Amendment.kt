package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Amendment(

        @JsonProperty("rationale") @NotNull
        val rationale: String,

        @JsonProperty("description")
        val description: String?
)