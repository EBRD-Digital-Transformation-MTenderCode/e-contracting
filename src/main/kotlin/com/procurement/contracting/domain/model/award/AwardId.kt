package com.procurement.contracting.domain.model.award

import com.procurement.contracting.extension.UUID_PATTERN
import com.procurement.contracting.extension.isUUID
import java.util.*

class AwardId private constructor(val underlying: UUID) {

    companion object {
        const val pattern = UUID_PATTERN
        fun validate(text: String): Boolean = text.isUUID
        fun orNull(text: String): AwardId? = if (validate(text)) AwardId(UUID.fromString(text)) else null
        fun generate() = AwardId(UUID.randomUUID())
    }

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is AwardId
                && this.underlying == other.underlying
        else
            true
    }

    override fun hashCode(): Int = underlying.hashCode()

    override fun toString(): String = underlying.toString()
}
