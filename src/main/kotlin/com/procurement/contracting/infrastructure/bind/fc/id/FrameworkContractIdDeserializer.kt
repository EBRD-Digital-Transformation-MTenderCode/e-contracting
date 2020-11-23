package com.procurement.contracting.infrastructure.bind.fc.id

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.contracting.domain.model.fc.id.FrameworkContractId

class FrameworkContractIdDeserializer : JsonDeserializer<FrameworkContractId>() {
    companion object {
        fun deserialize(text: String): FrameworkContractId = FrameworkContractId.orNull(text)
            ?: throw IllegalAccessException("Invalid format of the FC id. Expected: '${FrameworkContractId.pattern}', actual: '$text'.")
    }

    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): FrameworkContractId =
        deserialize(jsonParser.text)
}
