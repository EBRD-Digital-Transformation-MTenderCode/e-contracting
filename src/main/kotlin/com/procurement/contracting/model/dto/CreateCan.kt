package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.model.dto.ocds.Award
import com.procurement.contracting.model.dto.ocds.Can
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class CreateCanRQ @JsonCreator constructor(

        @field:Valid
        @field:NotNull
        @field:NotEmpty
        val awards: List<Award>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreateCanRS(

        val cans: List<Can>?
)