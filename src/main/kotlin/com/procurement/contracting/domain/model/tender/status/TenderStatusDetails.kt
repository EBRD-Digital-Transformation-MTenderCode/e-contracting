package com.procurement.contracting.domain.model.tender.status

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.exception.EnumException

enum class TenderStatusDetails(@JsonValue val value: String) {
    PRESELECTION("preselection"),
    PRESELECTED("preselected"),
    PREQUALIFICATION("prequalification"),
    PREQUALIFIED("prequalified"),
    EVALUATION("evaluation"),
    EVALUATED("evaluated"),
    EXECUTION("execution"),
    AWARDED("awarded"),
    PLANNING("planning"),
    PLANNED("planned"),
    ACTIVE("active"),
    BLOCKED("blocked"),
    CANCELLED("cancelled"),
    UNSUCCESSFUL("unsuccessful"),
    COMPLETE("complete"),
    WITHDRAWN("withdrawn"),
    SUSPENDED("suspended"),
    EMPTY("empty");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS: Map<String, TenderStatusDetails> = values().associateBy { it.value }

        fun fromString(value: String): TenderStatusDetails = CONSTANTS[value]
            ?: throw EnumException(
                enumType = TenderStatusDetails::class.java.name,
                value = value,
                values = values().toString()
            )
    }
}
