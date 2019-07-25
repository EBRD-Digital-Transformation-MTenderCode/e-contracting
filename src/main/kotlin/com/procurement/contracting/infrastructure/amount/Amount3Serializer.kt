package com.procurement.contracting.infrastructure.amount

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.io.IOException
import java.math.BigDecimal

class Amount3Serializer  :JsonSerializer<BigDecimal>() {
    companion object {
        fun serialize(amount: BigDecimal): String = "%.3f".format(amount)
    }

    @Throws(IOException::class, JsonProcessingException::class)
    override fun serialize(amount: BigDecimal, jsonGenerator: JsonGenerator, provider: SerializerProvider) =
        jsonGenerator.writeNumber(serialize(amount))
}