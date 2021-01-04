package com.procurement.contracting.domain.model.fc.status

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class FrameworkContractStatus(@JsonValue override val key: String) : EnumElementProvider.Element {

    CANCELLED("cancelled"),
    PENDING("pending"),
    ;

    override fun toString(): String = key

    companion object : EnumElementProvider<FrameworkContractStatus>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = FrameworkContractStatus.orThrow(name)
    }
}
