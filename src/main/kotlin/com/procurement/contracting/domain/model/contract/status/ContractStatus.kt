package com.procurement.contracting.domain.model.contract.status

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.exception.EnumException

enum class ContractStatus(@JsonValue val value: String) {
    PENDING("pending"),
    ACTIVE("active"),
    CANCELLED("cancelled"),
    COMPLETE("complete"),
    TERMINATED("terminated"),
    UNSUCCESSFUL("unsuccessful");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS: Map<String, ContractStatus> = values().associateBy { it.value.toUpperCase() }

        fun fromString(value: String): ContractStatus = CONSTANTS[value.toUpperCase()]
            ?: throw EnumException(
                enumType = ContractStatus::class.java.name,
                value = value,
                values = values().toString()
            )
    }
}
