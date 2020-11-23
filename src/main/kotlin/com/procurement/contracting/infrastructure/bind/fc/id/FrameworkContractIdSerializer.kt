package com.procurement.contracting.infrastructure.bind.fc.id

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.procurement.contracting.domain.model.fc.id.FrameworkContractId

class FrameworkContractIdSerializer : JsonSerializer<FrameworkContractId>() {
    companion object {
        fun serialize(id: FrameworkContractId): String = id.underlying
    }

    override fun serialize(id: FrameworkContractId, jsonGenerator: JsonGenerator, provider: SerializerProvider) =
        jsonGenerator.writeString(serialize(id))
}
