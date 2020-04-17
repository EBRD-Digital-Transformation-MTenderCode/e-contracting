package com.procurement.contracting.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.contracting.infrastructure.web.dto.Action

interface Handler<T : Action, R: Any> {
    val action: T
    fun handle(node: JsonNode): R
}