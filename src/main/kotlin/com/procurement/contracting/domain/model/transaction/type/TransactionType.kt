package com.procurement.contracting.domain.model.transaction.type

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.exception.EnumException

enum class TransactionType(@JsonValue val value: String) {
    ADVANCE("advance"),
    PAYMENT("payment");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS: Map<String, TransactionType> = values().associateBy { it.value }

        fun fromString(value: String): TransactionType = CONSTANTS[value]
            ?: throw EnumException(
                enumType = TransactionType::class.java.name,
                value = value,
                values = values().toString()
            )
    }
}
