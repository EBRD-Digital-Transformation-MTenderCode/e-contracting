package com.procurement.contracting.domain.model.related.process

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.exception.EnumException

enum class RelatedProcessType(@JsonValue val value: String) {
    FRAMEWORK("framework"),
    PLANNING("planning"),
    PARENT("parent"),
    PRIOR("prior"),
    UNSUCCESSFUL_PROCESS("unsuccessfulProcess"),
    SUB_CONTRACT("subContract"),
    REPLACEMENT_PROCESS("replacementProcess"),
    RENEWAL_PROCESS("renewalProcess");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS: Map<String, RelatedProcessType> = values().associateBy { it.value }

        fun fromString(value: String): RelatedProcessType = CONSTANTS[value]
            ?: throw EnumException(
                enumType = RelatedProcessType::class.java.name,
                value = value,
                values = values().toString()
            )
    }
}
