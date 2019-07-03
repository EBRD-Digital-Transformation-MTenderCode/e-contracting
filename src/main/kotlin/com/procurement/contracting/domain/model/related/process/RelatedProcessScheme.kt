package com.procurement.contracting.domain.model.related.process

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.exception.EnumException

enum class RelatedProcessScheme(@JsonValue val value: String) {
    OCID("ocid");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS: Map<String, RelatedProcessScheme> = values().associateBy { it.value }

        fun fromString(value: String): RelatedProcessScheme = CONSTANTS[value]
            ?: throw EnumException(
                enumType = RelatedProcessScheme::class.java.name,
                value = value,
                values = values().toString()
            )
    }
}
