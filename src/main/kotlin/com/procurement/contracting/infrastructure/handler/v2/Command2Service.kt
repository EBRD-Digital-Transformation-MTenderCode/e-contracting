package com.procurement.contracting.infrastructure.handler.v2

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.infrastructure.api.v2.ApiResponse2
import org.springframework.stereotype.Service

@Service
class Command2Service(
    private val logger: Logger,
    private val findCANIdsHandler: FindCANIdsHandler
) {

    fun execute(node: JsonNode): ApiResponse2 {
        val action = node.tryGetAction()
            .doReturn { error ->
                return generateResponseOnFailure(
                    fail = error,
                    id = node.tryGetId().get,
                    version = node.tryGetVersion().get,
                    logger = logger
                )
            }
        return when (action) {
            Command2Type.FIND_CAN_IDS -> findCANIdsHandler.handle(node)
        }
    }
}