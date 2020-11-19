package com.procurement.contracting.infrastructure.handler.v2.base

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.domain.functional.ValidationResult
import com.procurement.contracting.infrastructure.api.Action
import com.procurement.contracting.infrastructure.api.v2.ApiResponseV2
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.Handler
import com.procurement.contracting.infrastructure.handler.v2.generateResponseOnFailure
import com.procurement.contracting.infrastructure.handler.v2.tryGetId
import com.procurement.contracting.infrastructure.handler.v2.tryGetVersion

abstract class AbstractValidationHandler<ACTION : Action, E : Fail>(private val logger: Logger) : Handler<ACTION, ApiResponseV2> {

    override fun handle(node: JsonNode): ApiResponseV2 {
        val id = node.tryGetId().get
        val version = node.tryGetVersion().get

        return when (val result = execute(node)) {
            is ValidationResult.Ok -> {
                if (logger.isDebugEnabled)
                    logger.debug("${action.key} has been executed.")
                ApiResponseV2.Success(version = version, id = id)
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
