package com.procurement.contracting.infrastructure.bind.fc.id

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.contracting.domain.model.fc.id.FrameworkContractId

class FrameworkContractIdModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(FrameworkContractId::class.java, FrameworkContractIdSerializer())
        addDeserializer(FrameworkContractId::class.java, FrameworkContractIdDeserializer())
    }
}
