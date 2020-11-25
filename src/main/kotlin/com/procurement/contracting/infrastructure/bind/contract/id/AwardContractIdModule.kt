package com.procurement.contracting.infrastructure.bind.contract.id

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.contracting.domain.model.ac.id.AwardContractId

class AwardContractIdModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(AwardContractId::class.java, AwardContractIdSerializer())
        addDeserializer(AwardContractId::class.java, AwardContractIdDeserializer())
    }
}
