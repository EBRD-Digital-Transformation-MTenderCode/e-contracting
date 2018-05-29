package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.procurement.contracting.model.dto.databinding.MoneyDeserializer
import java.math.BigDecimal
import javax.validation.constraints.NotNull

data class BudgetSource(

        @JsonProperty("budgetBreakdownID") @NotNull
        val id: String,

        @JsonProperty("amount") @NotNull
        @JsonDeserialize(using = MoneyDeserializer::class)
        val amount: BigDecimal
)