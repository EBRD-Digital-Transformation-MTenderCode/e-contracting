package com.procurement.contracting.domain.model.confirmation.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class ConfirmationRequestSource(@JsonValue override val key: String) : EnumElementProvider.Element {
    BUYER("buyer"),
    TENDERER("tenderer"),
    APPROVE_BODY("approveBody");

    override fun toString(): String = key

    companion object : EnumElementProvider<ConfirmationRequestSource>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = ConfirmationRequestSource.orThrow(name)
    }
}
