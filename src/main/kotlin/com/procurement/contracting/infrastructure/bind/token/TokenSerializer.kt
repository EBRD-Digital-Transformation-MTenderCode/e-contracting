package com.procurement.contracting.infrastructure.bind.token

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.procurement.contracting.domain.model.Token

class TokenSerializer : JsonSerializer<Token>() {
    companion object {
        fun serialize(token: Token): String = token.underlying.toString()
    }

    override fun serialize(token: Token, jsonGenerator: JsonGenerator, provider: SerializerProvider) =
        jsonGenerator.writeString(serialize(token))
}
