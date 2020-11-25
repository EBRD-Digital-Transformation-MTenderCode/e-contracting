package com.procurement.contracting.domain.model.can

import com.procurement.contracting.extension.UUID_PATTERN
import com.procurement.contracting.extension.isUUID
import java.util.*

class CANId(val underlying: UUID) {

    companion object {
        const val pattern = UUID_PATTERN
        fun validate(text: String): Boolean = text.isUUID
        fun orNull(text: String): CANId? = if (validate(text)) CANId(UUID.fromString(text)) else null
        fun generate() = CANId(UUID.randomUUID())
    }

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is CANId
                && this.underlying == other.underlying
        else
            true
    }

    override fun hashCode(): Int = underlying.hashCode()

    override fun toString(): String = underlying.toString()
}
