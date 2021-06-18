package com.procurement.contracting.infrastructure.bind.po

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.procurement.contracting.domain.model.po.PurchasingOrderId

class PurchasingOrderIdSerializer : JsonSerializer<PurchasingOrderId>() {
    companion object {
        fun serialize(id: PurchasingOrderId): String = id.underlying.toString()
    }

    override fun serialize(id: PurchasingOrderId, jsonGenerator: JsonGenerator, provider: SerializerProvider) =
        jsonGenerator.writeString(serialize(id))
}
