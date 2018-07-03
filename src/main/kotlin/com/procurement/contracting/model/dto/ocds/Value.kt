package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.procurement.contracting.model.dto.databinding.MoneyDeserializer
import java.math.BigDecimal
import javax.validation.constraints.NotNull

data class Value @JsonCreator constructor(

        @field:NotNull
        @JsonDeserialize(using = MoneyDeserializer::class)
        val amount: BigDecimal,

        @field:NotNull
        val currency: Currency
)