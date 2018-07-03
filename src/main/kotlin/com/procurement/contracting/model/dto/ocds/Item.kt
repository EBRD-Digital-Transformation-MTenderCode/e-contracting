package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal
import javax.validation.Valid
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Item @JsonCreator constructor(

        @field:NotNull
        val id: String,

        @field:NotNull
        val description: String,

        @field:Valid
        @field:NotNull
        val classification: Classification,

        @field:Valid
        val additionalClassifications: Set<Classification>?,

        @field:NotNull
        val quantity: BigDecimal,

        @field:Valid
        @field:NotNull
        val unit: Unit,

        @field:NotNull
        val relatedLot: String
)