package com.procurement.contracting.domain.model.treasury

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.exception.EnumException

enum class TreasuryResponseStatus(@JsonValue val value: String) {
    APPROVED("3004"),
    NOT_ACCEPTED("3005"),
    REJECTED("3006");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS: Map<String, TreasuryResponseStatus> = values().associateBy { it.value }

        fun fromString(value: String): TreasuryResponseStatus = CONSTANTS[value]
            ?: throw EnumException(
                enumType = TreasuryResponseStatus::class.java.name,
                value = value,
                values = values().toString()
            )
    }
}
