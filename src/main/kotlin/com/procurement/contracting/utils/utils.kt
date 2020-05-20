package com.procurement.contracting.utils

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.procurement.contracting.domain.functional.Result
import com.procurement.contracting.infrastructure.apiversion.ApiVersion2Deserializer
import com.procurement.contracting.infrastructure.apiversion.ApiVersion2Serializer
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.web.dto.ApiVersion2
import com.procurement.contracting.model.dto.databinding.IntDeserializer
import com.procurement.contracting.model.dto.databinding.JsonDateDeserializer
import com.procurement.contracting.model.dto.databinding.JsonDateSerializer
import com.procurement.contracting.model.dto.databinding.StringsDeserializer
import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.*

private object JsonMapper {

    val mapper: ObjectMapper = ObjectMapper()
    var dateTimeFormatter: DateTimeFormatter

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

        mapper.registerModule(module)
        mapper.registerKotlinModule()
        mapper.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true)
        mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true)
        mapper.nodeFactory = JsonNodeFactory.withExactBigDecimals(true)

        dateTimeFormatter = DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral('T')
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .appendLiteral('Z')
            .toFormatter()
    }
}

/*Date utils*/
fun String.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.parse(this, JsonMapper.dateTimeFormatter)
}

fun LocalDateTime.toDate(): Date {
    return Date.from(this.toInstant(ZoneOffset.UTC))
}

fun Date.toLocal(): LocalDateTime {
    return LocalDateTime.ofInstant(this.toInstant(), ZoneOffset.UTC)
}

fun localNowUTC(): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC)
}

fun milliNowUTC(): Long {
    return localNowUTC().toInstant(ZoneOffset.UTC).toEpochMilli()
}

/*Json utils*/
fun <Any> toJson(obj: Any): String {
    try {
        return JsonMapper.mapper.writeValueAsString(obj)
    } catch (e: JsonProcessingException) {
        throw RuntimeException(e)
    }
}

fun <T> toObject(clazz: Class<T>, json: String): T {
    try {
        return JsonMapper.mapper.readValue(json, clazz)
    } catch (e: IOException) {
        throw IllegalArgumentException(e)
    }
}

fun <T> toObject(clazz: Class<T>, json: JsonNode): T {
    try {
        return JsonMapper.mapper.treeToValue(json, clazz)
    } catch (e: IOException) {
        throw IllegalArgumentException(e)
    }
}

fun <T : Any> JsonNode.tryToObject(target: Class<T>): Result<T, Fail.Incident.Transform.Parsing> = try {
    Result.success(JsonMapper.mapper.treeToValue(this, target))
} catch (expected: Exception) {
    Result.failure(Fail.Incident.Transform.Parsing(target.canonicalName, expected))
}

fun <T : Any> String.tryToObject(target: Class<T>): Result<T, Fail.Incident.Transform.Parsing> = try {
    Result.success(JsonMapper.mapper.readValue(this, target))
} catch (expected: Exception) {
    Result.failure(Fail.Incident.Transform.Parsing(target.canonicalName, expected))
}

fun String.tryToNode(): Result<JsonNode, Fail.Incident.Transform.Parsing> = try {
    Result.success(JsonMapper.mapper.readTree(this))
} catch (exception: JsonProcessingException) {
    Result.failure(Fail.Incident.Transform.Parsing(JsonNode::class.java.canonicalName, exception))
}
