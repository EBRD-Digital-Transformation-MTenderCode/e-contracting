package com.procurement.contracting.infrastructure.bind.confirmation

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestId

class ConfirmationRequestIdDeserializer : JsonDeserializer<ConfirmationRequestId>() {
    companion object {
        fun deserialize(text: String): ConfirmationRequestId = ConfirmationRequestId.orNull(text)
            ?: throw IllegalAccessException("Invalid format of the confirmationRequest id. Expected: '${Token.pattern}', actual: '$text'.")
    }

    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): ConfirmationRequestId =
        deserialize(jsonParser.text)
}
