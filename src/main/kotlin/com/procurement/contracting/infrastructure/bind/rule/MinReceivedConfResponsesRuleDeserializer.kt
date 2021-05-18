package com.procurement.contracting.infrastructure.bind.rule

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.fasterxml.jackson.databind.node.ObjectNode
import com.procurement.contracting.application.service.rule.model.MinReceivedConfResponsesRule
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import java.io.IOException

class MinReceivedConfResponsesRuleDeserializer : JsonDeserializer<MinReceivedConfResponsesRule>() {

    @Throws(IOException::class)
    override fun deserialize(
        jsonParser: JsonParser,
        deserializationContext: DeserializationContext
    ): MinReceivedConfResponsesRule {
        val quantityNode = jsonParser.readValueAs(ObjectNode::class.java)
        val quantity = quantityNode.get("quantity")
        return when (quantity.nodeType) {
            JsonNodeType.STRING -> MinReceivedConfResponsesRule.String.QuantityValue
                .creator(quantity.asText())
                .let { MinReceivedConfResponsesRule.String(it) }
            JsonNodeType.NUMBER -> MinReceivedConfResponsesRule.Number(Integer.valueOf(quantity.asText()))
            else -> throw ErrorException(ErrorType.JSON_TYPE, jsonParser.currentName)
        }
    }
}

