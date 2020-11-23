package com.procurement.contracting.infrastructure.bind.award.id

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.contracting.domain.model.award.AwardId

class AwardIdModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(AwardId::class.java, AwardIdSerializer())
        addDeserializer(AwardId::class.java, AwardIdDeserializer())
    }
}
