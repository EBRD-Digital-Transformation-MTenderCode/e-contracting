package com.procurement.contracting.domain.model.fc.id

import com.procurement.contracting.extension.UUID_PATTERN
import com.procurement.contracting.extension.isUUID
import java.util.*

class FrameworkContractId private constructor(val underlying: String) {

    companion object {
        const val pattern = UUID_PATTERN
        fun validate(text: String): Boolean = text.isUUID
        fun orNull(text: String): FrameworkContractId? =
            if (validate(text)) FrameworkContractId(UUID.fromString(text).toString()) else null

        fun generate() = FrameworkContractId(UUID.randomUUID().toString())
    }

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is FrameworkContractId
                && this.underlying == other.underlying
        else
            true
    }

    override fun hashCode(): Int = underlying.hashCode()

    override fun toString(): String = underlying
}
