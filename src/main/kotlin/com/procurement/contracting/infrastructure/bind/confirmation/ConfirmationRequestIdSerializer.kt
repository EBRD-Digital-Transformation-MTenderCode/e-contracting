package com.procurement.contracting.infrastructure.bind.confirmation

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestId

class ConfirmationRequestIdSerializer : JsonSerializer<ConfirmationRequestId>() {
    companion object {
        fun serialize(id: ConfirmationRequestId): String = id.underlying.toString()
    }

    override fun serialize(id: ConfirmationRequestId, jsonGenerator: JsonGenerator, provider: SerializerProvider) =
        jsonGenerator.writeString(serialize(id))
}
