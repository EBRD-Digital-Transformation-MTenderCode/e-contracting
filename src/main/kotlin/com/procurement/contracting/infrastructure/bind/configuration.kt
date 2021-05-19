package com.procurement.contracting.infrastructure.bind

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.procurement.contracting.application.service.rule.model.MinReceivedConfResponsesRule
import com.procurement.contracting.infrastructure.bind.api.command.id.CommandIdModule
import com.procurement.contracting.infrastructure.bind.api.version.ApiVersionModule
import com.procurement.contracting.infrastructure.bind.award.id.AwardIdModule
import com.procurement.contracting.infrastructure.bind.can.id.CANIdModule
import com.procurement.contracting.infrastructure.bind.confirmation.ConfirmationRequestIdModule
import com.procurement.contracting.infrastructure.bind.contract.id.AwardContractIdModule
import com.procurement.contracting.infrastructure.bind.date.JsonDateTimeModule
import com.procurement.contracting.infrastructure.bind.dynamic.DynamicValueModule
import com.procurement.contracting.infrastructure.bind.fc.id.FrameworkContractIdModule
import com.procurement.contracting.infrastructure.bind.lot.id.LotIdModule
import com.procurement.contracting.infrastructure.bind.owner.OwnerModule
import com.procurement.contracting.infrastructure.bind.rule.MinReceivedConfResponsesQuantityDeserializer
import com.procurement.contracting.infrastructure.bind.token.TokenModule

fun ObjectMapper.configuration() {
    val module = SimpleModule()
        .apply {
            addDeserializer(String::class.java, StringsDeserializer())
            addDeserializer(Int::class.java, IntDeserializer())
            addDeserializer(MinReceivedConfResponsesRule.Quantity::class.java, MinReceivedConfResponsesQuantityDeserializer())
        }

    registerModule(module)
    registerModule(ApiVersionModule())
    registerModule(CommandIdModule())
    registerModule(JsonDateTimeModule())
    registerModule(TokenModule())
    registerModule(ConfirmationRequestIdModule())
    registerModule(OwnerModule())
    registerModule(LotIdModule())
    registerModule(CANIdModule())
    registerModule(AwardIdModule())
    registerModule(AwardContractIdModule())
    registerModule(FrameworkContractIdModule())
    registerModule(DynamicValueModule())
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
