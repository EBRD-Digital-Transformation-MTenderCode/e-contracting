package com.procurement.contracting.domain.model.confirmation.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class ConfirmationRequestType(@JsonValue override val key: String) : EnumElementProvider.Key {
    DIGITAL_SIGNATURE("digitalSignature"),
    OUTSIDE_ACTION("outsideAction");

    override fun toString(): String = key

    companion object : EnumElementProvider<ConfirmationRequestType>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = ConfirmationRequestType.orThrow(name)
    }
}
