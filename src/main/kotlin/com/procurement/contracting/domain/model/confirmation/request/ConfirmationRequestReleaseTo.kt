package com.procurement.contracting.domain.model.confirmation.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class ConfirmationRequestReleaseTo(@JsonValue override val key: String) : EnumElementProvider.Key {
    DOCUMENT("document");

    override fun toString(): String = key

    companion object : EnumElementProvider<ConfirmationRequestReleaseTo>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = ConfirmationRequestReleaseTo.orThrow(name)
    }
}
