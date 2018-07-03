package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import javax.validation.Valid
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Can @JsonCreator constructor(

        @field:NotNull
        val token: String,

        @field:Valid
        @field:NotNull
        val contract: Contract
)
