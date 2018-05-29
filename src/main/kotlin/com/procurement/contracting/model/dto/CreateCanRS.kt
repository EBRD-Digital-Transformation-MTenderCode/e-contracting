package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateCanRS(

        @JsonProperty("cans")
        val cans: List<Can>?
)
