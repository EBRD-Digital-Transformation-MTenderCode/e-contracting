package com.procurement.contracting.domain.model.contract.status

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class ContractStatusDetails(@JsonValue override val key: String) : EnumElementProvider.Key {
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

    override fun toString(): String = key

    companion object : EnumElementProvider<ContractStatusDetails>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = ContractStatusDetails.orThrow(name)
    }
}