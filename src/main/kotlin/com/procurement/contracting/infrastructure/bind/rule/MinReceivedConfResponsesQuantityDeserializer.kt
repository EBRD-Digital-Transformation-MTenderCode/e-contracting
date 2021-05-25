package com.procurement.contracting.infrastructure.bind.rule

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.contracting.application.service.rule.model.MinReceivedConfResponsesRule
import java.io.IOException

class MinReceivedConfResponsesQuantityDeserializer : JsonDeserializer<MinReceivedConfResponsesRule.Quantity>() {

    @Throws(IOException::class)
    override fun deserialize(
        jsonParser: JsonParser,
        deserializationContext: DeserializationContext
    ): MinReceivedConfResponsesRule.Quantity =
        when (val token = jsonParser.currentToken) {
            JsonToken.VALUE_NUMBER_INT -> MinReceivedConfResponsesRule.Quantity.Number(jsonParser.text.toInt())
            JsonToken.VALUE_STRING -> MinReceivedConfResponsesRule.Quantity.All.of(jsonParser.text)
                ?: throw IllegalArgumentException("Invalid value of token '$$token'.")
            else -> throw IllegalArgumentException("Invalid type of token '$$token'.")
        }
}