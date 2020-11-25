package com.procurement.contracting.domain.model

import com.procurement.contracting.extension.UUID_PATTERN
import com.procurement.contracting.extension.isUUID
import java.util.*

class Token private constructor(val underlying: UUID) {

    companion object {
        const val pattern = UUID_PATTERN
        fun validate(text: String): Boolean = text.isUUID
        fun orNull(text: String): Token? = if (validate(text)) Token(UUID.fromString(text)) else null
        fun generate() = Token(UUID.randomUUID())
    }

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is Token
                && this.underlying == other.underlying
        else
            true
    }

    override fun hashCode(): Int = underlying.hashCode()

    override fun toString(): String = underlying.toString()
}
