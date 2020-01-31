package com.procurement.contracting.infrastructure.bind.apiversion

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.procurement.contracting.infrastructure.web.dto.ApiVersion
import java.io.IOException

class ApiVersionSerializer : JsonSerializer<ApiVersion>() {
    companion object {
        fun serialize(apiVersion: ApiVersion): String = "${apiVersion.major}.${apiVersion.minor}.${apiVersion.patch}"
    }

    @Throws(IOException::class, JsonProcessingException::class)
    override fun serialize(apiVersion: ApiVersion, jsonGenerator: JsonGenerator, provider: SerializerProvider) {
        jsonGenerator.writeString(serialize(apiVersion))
    }
}
