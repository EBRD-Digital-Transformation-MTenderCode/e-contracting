package com.procurement.contracting.infrastructure.bind.dynamic

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.procurement.contracting.domain.model.DynamicValue

class DynamicValueSerializer : JsonSerializer<DynamicValue>() {

    override fun serialize(value: DynamicValue, gen: JsonGenerator, serializers: SerializerProvider?) {
        when (value) {
            is DynamicValue.Boolean -> gen.writeBoolean(value.underlying)
            is DynamicValue.String -> gen.writeString(value.underlying)
            is DynamicValue.Integer -> gen.writeNumber(value.underlying)
            is DynamicValue.Number -> gen.writeNumber(value.underlying)
        }
    }
}
