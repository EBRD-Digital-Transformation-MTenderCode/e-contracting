package com.procurement.contracting.application.service.rule.model

import com.fasterxml.jackson.annotation.JsonProperty

data class MinReceivedConfResponsesRule(
    @field:JsonProperty("quantity") @param:JsonProperty("quantity") val quantity: Quantity
) {
    sealed class Quantity {
        data class Number(val underlying: Int) : Quantity()
        class All private constructor() : Quantity() {
            companion object {
                fun of(text: String): All? = if (text.toUpperCase() == "ALL") All() else null
            }
        }
    }
}
