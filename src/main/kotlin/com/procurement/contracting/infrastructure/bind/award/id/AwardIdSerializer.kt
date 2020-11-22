package com.procurement.contracting.infrastructure.bind.award.id

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.procurement.contracting.domain.model.award.AwardId

class AwardIdSerializer : JsonSerializer<AwardId>() {
    companion object {
        fun serialize(awardId: AwardId): String = awardId.underlying.toString()
    }

    override fun serialize(awardId: AwardId, jsonGenerator: JsonGenerator, provider: SerializerProvider) =
        jsonGenerator.writeString(serialize(awardId))
}
