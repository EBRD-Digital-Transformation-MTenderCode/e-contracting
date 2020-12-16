package com.procurement.contracting.domain.model.treasury

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class TreasuryResponseStatus(@JsonValue override val key: String) : EnumElementProvider.Element {
    APPROVED("3004"),
    NOT_ACCEPTED("3005"),
    REJECTED("3006");

    override fun toString(): String = key

    companion object : EnumElementProvider<TreasuryResponseStatus>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = TreasuryResponseStatus.orThrow(name)
    }
}