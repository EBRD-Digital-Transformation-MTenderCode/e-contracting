package com.procurement.contracting.infrastructure.handler.v2

import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.infrastructure.api.v2.ApiResponseV2
import com.procurement.contracting.infrastructure.api.v2.CommandTypeV2
import com.procurement.contracting.infrastructure.fail.error.BadRequest
import com.procurement.contracting.infrastructure.handler.v2.model.ApiResponseV2Generator.generateResponseOnFailure
import com.procurement.contracting.infrastructure.handler.v2.model.CommandDescriptor
import org.springframework.stereotype.Service

@Service
class CommandServiceV2(
    private val logger: Logger,
    private val findCANIdsHandler: FindCANIdsHandler
) {

    fun execute(descriptor: CommandDescriptor): ApiResponseV2 {
        return when (descriptor.action) {
            CommandTypeV2.FIND_CAN_IDS -> findCANIdsHandler.handle(descriptor)

            else -> {
                val errorDescription = "Unknown action '${descriptor.action.key}'."
                generateResponseOnFailure(
                    fail = BadRequest(description = errorDescription, exception = RuntimeException(errorDescription)),
                    id = descriptor.id,
                    version = descriptor.version,
                    logger = logger
                )
            }
        }
    }
}
