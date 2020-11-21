package com.procurement.contracting.infrastructure.bind.lot.id

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.procurement.contracting.domain.model.lot.LotId

class LotIdSerializer : JsonSerializer<LotId>() {
    companion object {
        fun serialize(lotId: LotId): String = lotId.underlying
    }

    override fun serialize(lotId: LotId, jsonGenerator: JsonGenerator, provider: SerializerProvider) =
        jsonGenerator.writeString(serialize(lotId))
}
