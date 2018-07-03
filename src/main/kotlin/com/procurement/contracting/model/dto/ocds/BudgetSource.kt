package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.procurement.contracting.model.dto.databinding.MoneyDeserializer
import java.math.BigDecimal
import javax.validation.constraints.NotNull

data class BudgetSource @JsonCreator constructor(

        @field:NotNull
        val id: String,

        @field:NotNull
        @field: JsonDeserialize(using = MoneyDeserializer::class)
        val amount: BigDecimal
)