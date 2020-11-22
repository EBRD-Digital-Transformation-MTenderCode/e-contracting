package com.procurement.contracting.infrastructure.bind.contract.id

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.procurement.contracting.domain.model.contract.id.ContractId

class ContractIdSerializer : JsonSerializer<ContractId>() {
    companion object {
        fun serialize(contractId: ContractId): String = contractId.underlying
    }

    override fun serialize(contractId: ContractId, jsonGenerator: JsonGenerator, provider: SerializerProvider) =
        jsonGenerator.writeString(serialize(contractId))
}
