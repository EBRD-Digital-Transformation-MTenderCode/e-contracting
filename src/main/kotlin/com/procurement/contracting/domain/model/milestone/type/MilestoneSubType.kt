package com.procurement.contracting.domain.model.milestone.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class MilestoneSubType(@JsonValue override val key: String) : EnumElementProvider.Element {
    BUYERS_APPROVAL("buyersApproval"),
    SUPPLIERS_APPROVAL("suppliersApproval"),
    CONTRACT_ACTIVATION("contractActivation"),
    APPROVE_BODY_VALIDATION("approveBodyValidation");

    override fun toString(): String = key

    companion object : EnumElementProvider<MilestoneSubType>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = MilestoneSubType.orThrow(name)
    }
}
