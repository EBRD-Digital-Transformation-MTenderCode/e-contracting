package com.procurement.contracting.domain.model.contract.status

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class ContractStatus(@JsonValue override val key: String) : EnumElementProvider.Key {
    PENDING("pending"),
    ACTIVE("active"),
    CANCELLED("cancelled"),
    COMPLETE("complete"),
    TERMINATED("terminated"),
    UNSUCCESSFUL("unsuccessful");

    override fun toString(): String = key

    companion object : EnumElementProvider<ContractStatus>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = ContractStatus.orThrow(name)
    }
}
