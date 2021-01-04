package com.procurement.contracting.infrastructure.bind.dynamic

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.contracting.domain.model.DynamicValue

class DynamicValueModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(DynamicValue::class.java, DynamicValueSerializer())
        addDeserializer(DynamicValue::class.java, DynamicValueDeserializer())
    }
}
