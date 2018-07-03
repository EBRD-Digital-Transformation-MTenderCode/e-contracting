package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import javax.validation.constraints.NotNull

data class Unit @JsonCreator constructor(

        @field:NotNull
        val id: String,

        @field:NotNull
        val name: String
)