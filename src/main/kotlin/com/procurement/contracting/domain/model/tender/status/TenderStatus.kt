package com.procurement.contracting.domain.model.tender.status

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.exception.EnumException

enum class TenderStatus(@JsonValue val value: String) {
    PLANNING("planning"),
    PLANNED("planned"),
    ACTIVE("active"),
    CANCELLED("cancelled"),
    UNSUCCESSFUL("unsuccessful"),
    COMPLETE("complete"),
    WITHDRAWN("withdrawn");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS: Map<String, TenderStatus> = values().associateBy { it.value }

        fun fromString(value: String): TenderStatus = CONSTANTS[value]
            ?: throw EnumException(
                enumType = TenderStatus::class.java.name,
                value = value,
                values = values().toString()
            )
    }
}
