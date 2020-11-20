package com.procurement.contracting.infrastructure.bind.token

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.contracting.domain.model.Token

class TokenDeserializer : JsonDeserializer<Token>() {
    companion object {
        fun deserialize(text: String): Token = Token.orNull(text)
            ?: throw IllegalAccessException("Invalid format of the token. Expected: '${Token.pattern}', actual: '$text'.")
    }

    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): Token =
        deserialize(jsonParser.text)
}
