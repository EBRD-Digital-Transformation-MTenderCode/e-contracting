package com.procurement.contracting.infrastructure.bind.can.id

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.contracting.domain.model.can.CANId

class CANIdDeserializer : JsonDeserializer<CANId>() {
    companion object {
        fun deserialize(text: String): CANId = CANId.orNull(text)
            ?: throw IllegalAccessException("Invalid format of the CAN id. Expected: '${CANId.pattern}', actual: '$text'.")
    }

    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): CANId =
        deserialize(jsonParser.text)
}
