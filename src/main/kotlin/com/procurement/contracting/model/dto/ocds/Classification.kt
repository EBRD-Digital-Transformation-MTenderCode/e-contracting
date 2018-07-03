package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import javax.validation.constraints.NotNull

data class Classification @JsonCreator constructor(

        @field:NotNull
        val scheme: Scheme,

        @field:NotNull
        val id: String,

        @field:NotNull
        val description: String,

        val uri: String?
)