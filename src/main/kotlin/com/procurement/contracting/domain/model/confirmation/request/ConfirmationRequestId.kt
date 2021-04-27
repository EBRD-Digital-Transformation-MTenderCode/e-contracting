package com.procurement.contracting.domain.model.confirmation.request

import com.procurement.contracting.extension.UUID_PATTERN
import com.procurement.contracting.extension.isUUID
import java.util.*

class ConfirmationRequestId private constructor(val underlying: UUID) {

    companion object {
        const val pattern = UUID_PATTERN
        fun validate(text: String): Boolean = text.isUUID
        fun orNull(text: String): ConfirmationRequestId? = if (validate(text)) ConfirmationRequestId(UUID.fromString(text)) else null
        fun generate() = ConfirmationRequestId(UUID.randomUUID())
    }

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is ConfirmationRequestId
                && this.underlying == other.underlying
        else
            true
    }

    override fun hashCode(): Int = underlying.hashCode()

    override fun toString(): String = underlying.toString()
}
