package com.procurement.contracting.domain.model.confirmation.response

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.exception.EnumException

enum class ConfirmationResponseType(@JsonValue val value: String) {
    DOCUMENT("document"),
    CODE("code");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS: Map<String, ConfirmationResponseType> = values().associateBy { it.value }

        fun fromString(value: String): ConfirmationResponseType = CONSTANTS[value]
            ?: throw EnumException(
                enumType = ConfirmationResponseType::class.java.name,
                value = value,
                values = values().toString()
            )
    }
}
