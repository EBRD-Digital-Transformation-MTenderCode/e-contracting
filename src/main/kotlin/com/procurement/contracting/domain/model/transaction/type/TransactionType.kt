package com.procurement.contracting.domain.model.transaction.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class TransactionType(@JsonValue override val key: String) : EnumElementProvider.Key {
    ADVANCE("advance"),
    PAYMENT("payment");

    override fun toString(): String = key

    companion object : EnumElementProvider<TransactionType>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = TransactionType.orThrow(name)
    }
}
