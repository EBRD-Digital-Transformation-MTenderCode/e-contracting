package com.procurement.contracting.domain.model.confirmation.request

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.exception.EnumException

enum class ConfirmationRequestReleaseTo(@JsonValue val value: String) {
    DOCUMENT("document");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS: Map<String, ConfirmationRequestReleaseTo> = values().associateBy { it.value }

        fun fromString(value: String): ConfirmationRequestReleaseTo = CONSTANTS[value]
            ?: throw EnumException(
                enumType = ConfirmationRequestReleaseTo::class.java.name,
                value = value,
                values = values().toString()
            )
    }
}
