package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Lot @JsonCreator constructor(

        @field:NotNull
        val id: String,

        @field:NotNull
        val title: String,

        @field:NotNull
        val description: String,

        val status: TenderStatus?,

        val statusDetails: TenderStatusDetails?
)