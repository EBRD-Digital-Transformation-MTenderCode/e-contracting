package com.procurement.contracting.domain.model.po

import com.procurement.contracting.extension.UUID_PATTERN
import com.procurement.contracting.extension.isUUID
import java.util.*

class PurchasingOrderId(val underlying: UUID) {

    companion object {
        const val pattern = UUID_PATTERN
        fun validate(text: String): Boolean = text.isUUID
        fun orNull(text: String): PurchasingOrderId? = if (validate(text)) PurchasingOrderId(UUID.fromString(text)) else null
        fun generate() = PurchasingOrderId(UUID.randomUUID())
    }

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is PurchasingOrderId
                && this.underlying == other.underlying
        else
            true
    }

    override fun hashCode(): Int = underlying.hashCode()

    override fun toString(): String = underlying.toString()
}
