package com.procurement.contracting.domain.model

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.exception.EnumException

enum class MainProcurementCategory(@JsonValue val value: String) {
    GOODS("goods"),
    SERVICES("services"),
    WORKS("works");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS: Map<String, MainProcurementCategory> = values().associateBy { it.value }

        fun fromString(value: String): MainProcurementCategory = CONSTANTS[value]
            ?: throw EnumException(
                enumType = MainProcurementCategory::class.java.name,
                value = value,
                values = values().toString()
            )
    }
}
