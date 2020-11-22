package com.procurement.contracting.infrastructure.bind.contract.id

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.contracting.domain.model.contract.id.ContractId
import com.procurement.contracting.domain.model.lot.LotId

class ContractIdDeserializer : JsonDeserializer<ContractId>() {
    companion object {
        fun deserialize(text: String): ContractId = ContractId.orNull(text)
            ?: throw IllegalAccessException("Invalid format of the contract id. Expected: '${LotId.pattern}', actual: '$text'.")
    }

    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): ContractId =
        deserialize(jsonParser.text)
}
