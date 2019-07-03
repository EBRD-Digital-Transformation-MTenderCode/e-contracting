package com.procurement.contracting.domain.model.milestone.status

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.exception.EnumException

enum class MilestoneStatus(@JsonValue val value: String) {
    SCHEDULED("scheduled"),
    MET("met");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS: Map<String, MilestoneStatus> = values().associateBy { it.value }

        fun fromString(value: String): MilestoneStatus = CONSTANTS[value]
            ?: throw EnumException(
                enumType = MilestoneStatus::class.java.name,
                value = value,
                values = values().toString()
            )
    }
}
