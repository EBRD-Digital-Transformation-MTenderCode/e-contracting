package com.procurement.contracting.infrastructure.handler.v2

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.infrastructure.api.ApiVersion
import com.procurement.contracting.infrastructure.api.command.id.CommandId
import com.procurement.contracting.infrastructure.api.v2.ApiResponseV2
import com.procurement.contracting.infrastructure.api.v2.CommandTypeV2
import org.springframework.stereotype.Service

@Service
class Command2Service(
    private val logger: Logger,
    private val findCANIdsHandler: FindCANIdsHandler
) {

    fun execute(node: JsonNode): ApiResponseV2 {
        val action = node.tryGetAction()
            .onFailure {
                return generateResponseOnFailure(
                    fail = it.reason,
                    id = node.tryGetId().getOrElse(CommandId.NaN),
                    version = node.tryGetVersion().getOrElse(ApiVersion.NaN),
                    logger = logger
                )
            }
        return when (action) {
            CommandTypeV2.FIND_CAN_IDS -> findCANIdsHandler.handle(node)
        }
    }
}