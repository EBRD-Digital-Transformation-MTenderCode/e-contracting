package com.procurement.contracting.domain.model.lot

import com.procurement.contracting.extension.UUID_PATTERN
import com.procurement.contracting.extension.isUUID

class LotId private constructor(val underlying: String) {

    companion object {
        const val pattern = UUID_PATTERN
        fun validate(text: String): Boolean = text.isUUID
        fun orNull(text: String): LotId? = if (validate(text)) LotId(text) else null
    }

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is LotId
                && this.underlying == other.underlying
        else
            true
    }

    override fun hashCode(): Int = underlying.hashCode()

    override fun toString(): String = underlying
}
