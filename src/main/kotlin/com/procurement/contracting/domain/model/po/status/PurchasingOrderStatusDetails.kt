package com.procurement.contracting.domain.model.po.status

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class PurchasingOrderStatusDetails(@JsonValue override val key: String) : EnumElementProvider.Element {
    CONTRACT_PROJECT("contractProject");

    override fun toString(): String = key

    companion object : EnumElementProvider<PurchasingOrderStatusDetails>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = PurchasingOrderStatusDetails.orThrow(name)
    }
}
