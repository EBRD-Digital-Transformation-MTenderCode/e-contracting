package com.procurement.contracting.infrastructure.bind.po

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.contracting.domain.model.po.PurchasingOrderId

class PurchasingOrderIdDeserializer : JsonDeserializer<PurchasingOrderId>() {
    companion object {
        fun deserialize(text: String): PurchasingOrderId = PurchasingOrderId.orNull(text)
            ?: throw IllegalAccessException("Invalid format of the purchasing order id. Expected: '${PurchasingOrderId.pattern}', actual: '$text'.")
    }

    override fun deserialize(
        jsonParser: JsonParser,
        deserializationContext: DeserializationContext
    ): PurchasingOrderId =
        deserialize(jsonParser.text)
}
