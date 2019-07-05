package com.procurement.contracting.domain.model.milestone.type

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.exception.EnumException

enum class MilestoneType(@JsonValue val value: String) {
    DELIVERY("delivery"),
    X_WARRANTY("x_warranty"),
    X_REPORTING("x_reporting"),
    APPROVAL("approval");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS: Map<String, MilestoneType> = values().associateBy { it.value }

        fun fromString(value: String): MilestoneType = CONSTANTS[value]
            ?: throw EnumException(
                enumType = MilestoneType::class.java.name,
                value = value,
                values = values().toString()
            )
    }
}
