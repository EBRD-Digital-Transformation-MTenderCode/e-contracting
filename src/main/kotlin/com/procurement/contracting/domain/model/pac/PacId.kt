package com.procurement.contracting.domain.model.pac

import com.procurement.contracting.extension.UUID_PATTERN
import com.procurement.contracting.extension.isUUID
import java.util.*

class PacId private constructor(val underlying: String) {

    companion object {
        const val pattern = UUID_PATTERN
        fun validate(text: String): Boolean = text.isUUID
        fun orNull(text: String): PacId? =
            if (validate(text)) PacId(text) else null

        fun generate() = PacId(UUID.randomUUID().toString())
    }

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is PacId
                && this.underlying == other.underlying
        else
            true
    }

    override fun hashCode(): Int = underlying.hashCode()

    override fun toString(): String = underlying
}
