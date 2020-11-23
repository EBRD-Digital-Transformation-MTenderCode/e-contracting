package com.procurement.contracting.infrastructure.bind.lot.id

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.contracting.domain.model.lot.LotId

class LotIdDeserializer : JsonDeserializer<LotId>() {
    companion object {
        fun deserialize(text: String): LotId = LotId.orNull(text)
            ?: throw IllegalAccessException("Invalid format of the lot id. Expected: '${LotId.pattern}', actual: '$text'.")
    }

    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): LotId =
        deserialize(jsonParser.text)
}
