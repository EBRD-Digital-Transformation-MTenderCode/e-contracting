package com.procurement.contracting.infrastructure.bind

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import java.io.IOException

@Deprecated(message = "Need remove")
class BooleansDeserializer : JsonDeserializer<Boolean>() {

    @Throws(IOException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): Boolean {
        if (!jsonParser.currentToken.isBoolean) {
            throw ErrorException(ErrorType.JSON_TYPE, jsonParser.currentName)
        }
        return jsonParser.valueAsBoolean
    }
}