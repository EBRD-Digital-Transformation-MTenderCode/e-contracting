package com.procurement.contracting.infrastructure.bind.contract.id

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.procurement.contracting.domain.model.ac.id.AwardContractId

class AwardContractIdSerializer : JsonSerializer<AwardContractId>() {
    companion object {
        fun serialize(id: AwardContractId): String = id.underlying
    }

    override fun serialize(id: AwardContractId, jsonGenerator: JsonGenerator, provider: SerializerProvider) =
        jsonGenerator.writeString(serialize(id))
}
