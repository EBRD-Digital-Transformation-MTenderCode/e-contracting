package com.procurement.contracting.infrastructure.bind.api.version

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.contracting.infrastructure.api.ApiVersion

class ApiVersionModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(ApiVersion::class.java, ApiVersionSerializer())
        addDeserializer(ApiVersion::class.java, ApiVersionDeserializer())
    }
}
