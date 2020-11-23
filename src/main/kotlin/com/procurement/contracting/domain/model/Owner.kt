package com.procurement.contracting.domain.model

import com.procurement.contracting.extension.UUID_PATTERN
import com.procurement.contracting.extension.isUUID

class Owner private constructor(val underlying: String) {

    companion object {
        const val pattern = UUID_PATTERN
        fun validate(text: String): Boolean = text.isUUID
        fun orNull(text: String): Owner? = if (validate(text)) Owner(text) else null
    }

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is Owner
                && this.underlying == other.underlying
        else
            true
    }

    override fun hashCode(): Int = underlying.hashCode()

    override fun toString(): String = underlying
}
