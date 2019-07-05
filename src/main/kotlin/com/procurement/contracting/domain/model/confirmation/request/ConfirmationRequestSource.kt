package com.procurement.contracting.domain.model.confirmation.request

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.exception.EnumException

enum class ConfirmationRequestSource(@JsonValue val value: String) {
    BUYER("buyer"),
    TENDERER("tenderer"),
    APPROVE_BODY("approveBody");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS: Map<String, ConfirmationRequestSource> = values().associateBy { it.value }

        fun fromString(value: String): ConfirmationRequestSource = CONSTANTS[value]
            ?: throw EnumException(
                enumType = ConfirmationRequestSource::class.java.name,
                value = value,
                values = values().toString()
            )
    }
}
