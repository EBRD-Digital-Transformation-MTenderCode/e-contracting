package com.procurement.contracting.infrastructure.bind.dynamic

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.contracting.domain.model.DynamicValue
import java.math.BigDecimal

class DynamicValueDeserializer : JsonDeserializer<DynamicValue>() {

    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): DynamicValue =
        when (val token = jsonParser.currentToken) {
            JsonToken.VALUE_TRUE -> DynamicValue.Boolean(true)
            JsonToken.VALUE_FALSE -> DynamicValue.Boolean(false)
            JsonToken.VALUE_STRING -> DynamicValue.String(jsonParser.text)
            JsonToken.VALUE_NUMBER_INT -> DynamicValue.Integer(jsonParser.text.toLong())
            JsonToken.VALUE_NUMBER_FLOAT -> DynamicValue.Number(BigDecimal(jsonParser.text))
            else -> throw IllegalArgumentException("Invalid value of token '$$token'.")
        }
}
