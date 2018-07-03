package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.contracting.model.dto.ocds.Award
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class CreateCanRQ @JsonCreator constructor(

        @field:Valid
        @field:NotNull
        @field:NotEmpty
        val awards: List<Award>
)
