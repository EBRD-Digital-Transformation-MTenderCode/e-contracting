package com.procurement.contracting.domain.model.document.type

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.exception.EnumException

enum class DocumentTypeUpdateCan(@JsonValue val value: String) {
    EVALUATION_REPORT("evaluationReports");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS: Map<String, DocumentTypeUpdateCan> = values().associateBy { it.value }

        fun fromString(value: String): DocumentTypeUpdateCan = CONSTANTS[value]
            ?: throw EnumException(
                enumType = DocumentTypeUpdateCan::class.java.name,
                value = value,
                values = values().toString()
            )
    }
}
