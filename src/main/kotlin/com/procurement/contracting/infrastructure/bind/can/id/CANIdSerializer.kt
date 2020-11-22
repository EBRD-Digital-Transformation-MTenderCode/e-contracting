package com.procurement.contracting.infrastructure.bind.can.id

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.procurement.contracting.domain.model.can.CANId

class CANIdSerializer : JsonSerializer<CANId>() {
    companion object {
        fun serialize(canId: CANId): String = canId.underlying.toString()
    }

    override fun serialize(canId: CANId, jsonGenerator: JsonGenerator, provider: SerializerProvider) =
        jsonGenerator.writeString(serialize(canId))
}
