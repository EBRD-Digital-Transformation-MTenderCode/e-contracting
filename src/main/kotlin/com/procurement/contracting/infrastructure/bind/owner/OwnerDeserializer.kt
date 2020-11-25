package com.procurement.contracting.infrastructure.bind.owner

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.contracting.domain.model.Owner

class OwnerDeserializer : JsonDeserializer<Owner>() {
    companion object {
        fun deserialize(text: String): Owner = Owner.orNull(text)
            ?: throw IllegalAccessException("Invalid format of the owner. Expected: '${Owner.pattern}', actual: '$text'.")
    }

    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): Owner =
        deserialize(jsonParser.text)
}
