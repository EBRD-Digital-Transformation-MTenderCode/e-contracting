package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal
import javax.validation.Valid
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Item @JsonCreator constructor(

        val id: String,

        val description: String,

        val classification: Classification,

        val additionalClassifications: Set<Classification>?,

        val quantity: BigDecimal,

        val unit: Unit,

        val relatedLot: String
)