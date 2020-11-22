package com.procurement.contracting.infrastructure.bind.contract.id

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.contracting.domain.model.contract.id.ContractId

class ContractIdModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(ContractId::class.java, ContractIdSerializer())
        addDeserializer(ContractId::class.java, ContractIdDeserializer())
    }
}
