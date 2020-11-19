package com.procurement.contracting.infrastructure.bind

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.procurement.contracting.infrastructure.bind.api.version.ApiVersionModule
import com.procurement.contracting.infrastructure.bind.date.JsonDateDeserializer
import com.procurement.contracting.infrastructure.bind.date.JsonDateSerializer
import java.time.LocalDateTime

fun ObjectMapper.configuration() {
    val module = SimpleModule()
        .apply {
            addDeserializer(String::class.java, StringsDeserializer())
            addDeserializer(Int::class.java, IntDeserializer())

            addSerializer(LocalDateTime::class.java, JsonDateSerializer())
            addDeserializer(LocalDateTime::class.java, JsonDateDeserializer())
        }

    registerModule(module)
    registerModule(ApiVersionModule())
    registerKotlinModule()

    configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, false)
    configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true)
    configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true)
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
    configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true)
    configure(MapperFeature.ALLOW_COERCION_OF_SCALARS, false)

    nodeFactory = JsonNodeFactory.withExactBigDecimals(true)
}
