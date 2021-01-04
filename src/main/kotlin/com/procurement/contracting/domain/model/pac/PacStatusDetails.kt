package com.procurement.contracting.domain.model.pac

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class PacStatusDetails(@JsonValue override val key: String) : EnumElementProvider.Element {

    CONCLUDED("concluded"),
    ALL_REJECTED("allRejected")
    ;

    override fun toString(): String = key

    companion object : EnumElementProvider<PacStatusDetails>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = PacStatusDetails.orThrow(name)
    }
}
