package com.procurement.contracting.infrastructure.bind.po

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.contracting.domain.model.po.PurchasingOrderId

class PurchasingOrderIdModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(PurchasingOrderId::class.java, PurchasingOrderIdSerializer())
        addDeserializer(PurchasingOrderId::class.java, PurchasingOrderIdDeserializer())
    }
}
