package com.procurement.contracting.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.domain.functional.ValidationResult
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.web.dto.Action
import com.procurement.contracting.infrastructure.web.dto.ApiResponse2
import com.procurement.contracting.infrastructure.web.dto.ApiSuccessResponse2
import com.procurement.contracting.model.dto.bpe.generateResponseOnFailure
import com.procurement.contracting.model.dto.bpe.tryGetId
import com.procurement.contracting.model.dto.bpe.tryGetVersion

abstract class AbstractValidationHandler<ACTION : Action, E : Fail>(private val logger: Logger) : Handler<ACTION, ApiResponse2> {

    override fun handle(node: JsonNode): ApiResponse2 {
        val id = node.tryGetId().get
        val version = node.tryGetVersion().get

        return when (val result = execute(node)) {
            is ValidationResult.Ok -> {
                if (logger.isDebugEnabled)
                    logger.debug("${action.key} has been executed.")
                ApiSuccessResponse2(version = version, id = id)
            }
            is ValidationResult.Fail -> generateResponseOnFailure(
                fail = result.error,
                version = version,
                id = id,
                logger = logger
            )
        }
    }

    abstract fun execute(node: JsonNode): ValidationResult<E>
}
