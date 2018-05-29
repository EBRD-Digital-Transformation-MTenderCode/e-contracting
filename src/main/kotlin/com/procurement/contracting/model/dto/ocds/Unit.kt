package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull

data class Unit(

        @JsonProperty("id") @NotNull
        val id: String,

        @JsonProperty("name") @NotNull
        val name: String
)