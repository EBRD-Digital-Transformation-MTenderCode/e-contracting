package com.procurement.contracting.service

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.infrastructure.web.dto.ApiResponse2
import com.procurement.contracting.model.dto.bpe.generateResponseOnFailure
import com.procurement.contracting.model.dto.bpe.tryGetAction
import com.procurement.contracting.model.dto.bpe.tryGetId
import com.procurement.contracting.model.dto.bpe.tryGetVersion
import org.springframework.stereotype.Service

@Service
class Command2Service(
    private val logger: Logger
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

        return TODO()
    }
}