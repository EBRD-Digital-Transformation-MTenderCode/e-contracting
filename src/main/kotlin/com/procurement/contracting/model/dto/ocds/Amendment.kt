package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import javax.validation.constraints.NotNull

data class Amendment @JsonCreator constructor(

        @field:NotNull
        val rationale: String,

        val description: String?
)