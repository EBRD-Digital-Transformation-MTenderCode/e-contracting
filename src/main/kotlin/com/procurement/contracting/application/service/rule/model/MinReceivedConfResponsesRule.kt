package com.procurement.contracting.application.service.rule.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

sealed class MinReceivedConfResponsesRule {
    abstract val isNumber: Boolean



    data class Number(
        @field:JsonProperty("quantity") @param:JsonProperty("quantity") val quantity: Int
    ) : MinReceivedConfResponsesRule() {
        override val isNumber: Boolean = true
    }

    data class String(
        @field:JsonProperty("quantity") @param:JsonProperty("quantity") val quantity: QuantityValue
    ) : MinReceivedConfResponsesRule() {
        override val isNumber: Boolean = false

        enum class QuantityValue(@JsonValue override val key: kotlin.String) : EnumElementProvider.Element {
            ALL("all");

            override fun toString(): kotlin.String = key

            companion object : EnumElementProvider<QuantityValue>(info = info()) {
                @JvmStatic
                @JsonCreator
                fun creator(name: kotlin.String) = QuantityValue.orThrow(name)
            }
        }
    }
}
