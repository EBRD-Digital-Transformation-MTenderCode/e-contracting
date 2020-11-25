package com.procurement.contracting.infrastructure.bind.lot.id

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.contracting.domain.model.lot.LotId

class LotIdModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(LotId::class.java, LotIdSerializer())
        addDeserializer(LotId::class.java, LotIdDeserializer())
    }
}
