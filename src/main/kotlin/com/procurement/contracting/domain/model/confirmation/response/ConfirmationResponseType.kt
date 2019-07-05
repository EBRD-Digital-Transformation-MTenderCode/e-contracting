package com.procurement.contracting.domain.model.confirmation.response

import com.fasterxml.jackson.annotation.JsonValue

enum class ConfirmationResponseType(@JsonValue val value: String) {
    CODE("code"),
    DOCUMENT("document");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS: Map<String, ConfirmationResponseType> = values().associateBy {
            it.value
        }

        fun fromString(value: String): ConfirmationResponseType =
            CONSTANTS[value]
                ?: throw NoSuchElementException("ConfirmationRequestSource does not have an element with the value of '$value'")
    }
}
