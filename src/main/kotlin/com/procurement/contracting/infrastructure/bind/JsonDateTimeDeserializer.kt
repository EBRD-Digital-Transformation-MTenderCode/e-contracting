package com.procurement.contracting.infrastructure.bind

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.time.LocalDateTime

class JsonDateTimeDeserializer : JsonDeserializer<LocalDateTime>() {

    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): LocalDateTime =
        LocalDateTime.parse(jsonParser.text, JsonDateTimeFormatter.formatter)
}
