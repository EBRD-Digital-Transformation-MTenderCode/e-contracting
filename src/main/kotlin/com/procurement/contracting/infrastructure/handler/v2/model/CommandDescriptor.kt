package com.procurement.contracting.infrastructure.handler.v2.model

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.contracting.infrastructure.api.Action
import com.procurement.contracting.infrastructure.api.ApiVersion
import com.procurement.contracting.infrastructure.api.command.id.CommandId

data class CommandDescriptor(
    val version: ApiVersion,
    val id: CommandId,
    val action: Action,
    val body: Body
) {
    data class Body(val asString: String, val asJsonNode: JsonNode)

    companion object
}
