package com.procurement.contracting.domain.model.organization

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class Scale(@JsonValue override val key: String) : EnumElementProvider.Element {
    MICRO("micro"),
    SME("sme"),
    LARGE("large"),
    EMPTY("");

    override fun toString(): String = key

    companion object : EnumElementProvider<Scale>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = Scale.orThrow(name)
    }
}