package com.procurement.contracting.domain.model.po.status

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class PurchasingOrderStatus(@JsonValue override val key: String) : EnumElementProvider.Element {
    PENDING("pending");

    override fun toString(): String = key

    companion object : EnumElementProvider<PurchasingOrderStatus>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = PurchasingOrderStatus.orThrow(name)
    }
}
