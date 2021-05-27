package com.procurement.contracting.domain.model.pac

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class PacStatus(@JsonValue override val key: String) : EnumElementProvider.Element {

    PENDING("pending"),
    CANCELLED("cancelled"),
    ACTIVE("active")
    ;

    override fun toString(): String = key

    companion object : EnumElementProvider<PacStatus>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = PacStatus.orThrow(name)
    }
}
