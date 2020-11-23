package com.procurement.contracting.infrastructure.bind.contract.id

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.contracting.domain.model.ac.id.AwardContractId
import com.procurement.contracting.domain.model.lot.LotId

class AwardContractIdDeserializer : JsonDeserializer<AwardContractId>() {
    companion object {
        fun deserialize(text: String): AwardContractId = AwardContractId.orNull(text)
            ?: throw IllegalAccessException("Invalid format of the contract id. Expected: '${LotId.pattern}', actual: '$text'.")
    }

    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): AwardContractId =
        deserialize(jsonParser.text)
}
