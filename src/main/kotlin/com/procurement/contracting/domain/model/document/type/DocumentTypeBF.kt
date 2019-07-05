package com.procurement.contracting.domain.model.document.type

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.exception.EnumException

enum class DocumentTypeBF(@JsonValue val value: String) {
    REGULATORY_DOCUMENT("regulatoryDocument");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS: Map<String, DocumentTypeBF> = values().associateBy { it.value }

        fun fromString(value: String): DocumentTypeBF = CONSTANTS[value]
            ?: throw EnumException(
                enumType = DocumentTypeBF::class.java.name,
                value = value,
                values = values().toString()
            )
    }
}
