package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.contracting.model.dto.ocds.Contract
import javax.validation.Valid
import javax.validation.constraints.NotNull

data class Can @JsonCreator constructor(

        @field:NotNull
        val token: String,

        @field:Valid
        @field:NotNull
        val contract: Contract
)
