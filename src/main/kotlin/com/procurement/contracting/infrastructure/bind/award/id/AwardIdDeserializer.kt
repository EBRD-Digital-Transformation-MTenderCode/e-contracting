package com.procurement.contracting.infrastructure.bind.award.id

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.contracting.domain.model.award.AwardId

class AwardIdDeserializer : JsonDeserializer<AwardId>() {
    companion object {
        fun deserialize(text: String): AwardId = AwardId.orNull(text)
            ?: throw IllegalAccessException("Invalid format of the Award id. Expected: '${AwardId.pattern}', actual: '$text'.")
    }

    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): AwardId =
        deserialize(jsonParser.text)
}
