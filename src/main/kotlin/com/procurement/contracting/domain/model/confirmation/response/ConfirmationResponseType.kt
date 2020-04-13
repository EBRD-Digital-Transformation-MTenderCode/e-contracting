package com.procurement.contracting.domain.model.confirmation.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class ConfirmationResponseType(@JsonValue override val key: String) : EnumElementProvider.Key {
    CODE("code"),
    DOCUMENT("document");

    override fun toString(): String = key

    companion object : EnumElementProvider<ConfirmationResponseType>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = ConfirmationResponseType.orThrow(name)
    }
}
