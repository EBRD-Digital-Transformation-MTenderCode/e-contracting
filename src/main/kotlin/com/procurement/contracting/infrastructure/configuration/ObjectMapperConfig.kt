package com.procurement.contracting.infrastructure.configuration

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.procurement.contracting.infrastructure.api.v2.ApiVersion2
import com.procurement.contracting.infrastructure.bind.IntDeserializer
import com.procurement.contracting.infrastructure.bind.StringsDeserializer
import com.procurement.contracting.infrastructure.bind.api.version.ApiVersion2Deserializer
import com.procurement.contracting.infrastructure.bind.api.version.ApiVersion2Serializer
import com.procurement.contracting.infrastructure.bind.date.JsonDateDeserializer
import com.procurement.contracting.infrastructure.bind.date.JsonDateSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime

@Configuration
class ObjectMapperConfig(@Autowired objectMapper: ObjectMapper) {

    init {
        val module = SimpleModule()
        module.addSerializer(LocalDateTime::class.java, JsonDateSerializer())
        module.addDeserializer(LocalDateTime::class.java, JsonDateDeserializer())
        module.addDeserializer(String::class.java, StringsDeserializer())
        module.addDeserializer(Int::class.java, IntDeserializer())
        /**
         * Serializer/Deserializer for ApiVersion type
         */
        module.addSerializer(ApiVersion2::class.java, ApiVersion2Serializer())
        module.addDeserializer(ApiVersion2::class.java, ApiVersion2Deserializer())


        objectMapper.registerModule(module)
        objectMapper.registerKotlinModule()
        objectMapper.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true)
        objectMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        objectMapper.nodeFactory = JsonNodeFactory.withExactBigDecimals(true)
    }
}
