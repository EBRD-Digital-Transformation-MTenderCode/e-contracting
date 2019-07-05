package com.procurement.contracting.domain.model.milestone.type

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.exception.EnumException

enum class MilestoneSubType(@JsonValue val value: String) {
    BUYERS_APPROVAL("buyersApproval"),
    SUPPLIERS_APPROVAL("suppliersApproval"),
    CONTRACT_ACTIVATION("contractActivation"),
    APPROVE_BODY_VALIDATION("approveBodyValidation");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS: Map<String, MilestoneSubType> = values().associateBy { it.value }

        fun fromString(value: String): MilestoneSubType = CONSTANTS[value]
            ?: throw EnumException(
                enumType = MilestoneSubType::class.java.name,
                value = value,
                values = values().toString()
            )
    }
}
