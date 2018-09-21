package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Classification @JsonCreator constructor(

        @field:NotNull
        val scheme: String,

        @field:NotNull
        val id: String,

        @field:NotNull
        val description: String,

        val uri: String?
)