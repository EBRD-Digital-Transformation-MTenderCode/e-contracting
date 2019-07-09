package com.procurement.contracting.domain.model.can.status

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.exception.EnumException

enum class CANStatusDetails(@JsonValue val value: String) {
    CONTRACT_PROJECT("contractProject"),
    ACTIVE("active"),
    UNSUCCESSFUL("unsuccessful"),
    EMPTY("empty"),
    TREASURY_REJECTION("treasuryRejection");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS: Map<String, CANStatusDetails> = values().associateBy { it.value }

        fun fromString(value: String): CANStatusDetails = CONSTANTS[value]
            ?: throw EnumException(
                enumType = CANStatusDetails::class.java.name,
                value = value,
                values = values().toString()
            )
    }
}
