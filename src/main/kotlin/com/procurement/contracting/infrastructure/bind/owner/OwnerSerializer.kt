package com.procurement.contracting.infrastructure.bind.owner

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.procurement.contracting.domain.model.Owner

class OwnerSerializer : JsonSerializer<Owner>() {
    companion object {
        fun serialize(owner: Owner): String = owner.underlying
    }

    override fun serialize(owner: Owner, jsonGenerator: JsonGenerator, provider: SerializerProvider) =
        jsonGenerator.writeString(serialize(owner))
}
