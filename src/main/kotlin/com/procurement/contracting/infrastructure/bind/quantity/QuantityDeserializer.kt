package com.procurement.contracting.infrastructure.bind.quantity

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import java.io.IOException
import java.math.BigDecimal

class QuantityDeserializer : JsonDeserializer<BigDecimal>() {

    private val delegate = NumberDeserializers.BigDecimalDeserializer.instance

    @Throws(IOException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): BigDecimal {
        if (jsonParser.currentToken == JsonToken.VALUE_STRING) {
            throw ErrorException(ErrorType.JSON_TYPE, jsonParser.currentName)
        }
        var bd = delegate.deserialize(jsonParser, deserializationContext)
        if (bd <= BigDecimal.ZERO) throw ErrorException(ErrorType.JSON_TYPE, jsonParser.currentName)
        return bd
    }
}