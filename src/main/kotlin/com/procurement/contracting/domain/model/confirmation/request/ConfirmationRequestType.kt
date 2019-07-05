package com.procurement.contracting.domain.model.confirmation.request

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.exception.EnumException

enum class ConfirmationRequestType(@JsonValue val value: String) {
    DIGITAL_SIGNATURE("digitalSignature"),
    OUTSIDE_ACTION("outsideAction");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS: Map<String, ConfirmationRequestType> = values().associateBy { it.value }

        fun fromString(value: String): ConfirmationRequestType = CONSTANTS[value]
            ?: throw EnumException(
                enumType = ConfirmationRequestType::class.java.name,
                value = value,
                values = values().toString()
            )
    }
}
