package com.procurement.contracting.domain.model.can.status

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.exception.EnumException

enum class CANStatus(@JsonValue val value: String) {
    PENDING("pending"),//+
    ACTIVE("active"),//+
    CANCELLED("cancelled"), //+
    UNSUCCESSFUL("unsuccessful"); //+

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS: Map<String, CANStatus> = values().associateBy { it.value }

        fun fromString(value: String): CANStatus = CONSTANTS[value]
            ?: throw EnumException(
                enumType = CANStatus::class.java.name,
                value = value,
                values = values().toString()
            )
    }
}
