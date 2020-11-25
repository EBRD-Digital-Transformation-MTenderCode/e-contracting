package com.procurement.contracting.infrastructure.bind.can.id

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.contracting.domain.model.can.CANId

class CANIdModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(CANId::class.java, CANIdSerializer())
        addDeserializer(CANId::class.java, CANIdDeserializer())
    }
}
