package com.procurement.contracting.domain.model.contract.status

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.exception.EnumException

enum class ContractStatusDetails(@JsonValue val value: String) {
    CONTRACT_PROJECT("contractProject"),
    CONTRACT_PREPARATION("contractPreparation"),
    APPROVED("approved"),
    SIGNED("signed"),
    VERIFICATION("verification"),
    VERIFIED("verified"),
    ISSUED("issued"),
    APPROVEMENT("approvement"),
    EXECUTION("execution"),
    EMPTY("empty");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS: Map<String, ContractStatusDetails> = values().associateBy { it.value }

        fun fromString(value: String): ContractStatusDetails = CONSTANTS[value]
            ?: throw EnumException(
                enumType = ContractStatusDetails::class.java.name,
                value = value,
                values = values().toString()
            )
    }
}
