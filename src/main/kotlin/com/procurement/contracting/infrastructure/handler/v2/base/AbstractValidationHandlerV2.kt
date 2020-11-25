package com.procurement.contracting.infrastructure.handler.v2.base

import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.infrastructure.api.v2.ApiResponseV2
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.Handler
import com.procurement.contracting.infrastructure.handler.v2.model.ApiResponseV2Generator.generateResponseOnFailure
import com.procurement.contracting.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.contracting.lib.functional.ValidationResult

abstract class AbstractValidationHandlerV2(private val logger: Logger) : Handler<ApiResponseV2> {

    override fun handle(descriptor: CommandDescriptor): ApiResponseV2 =
        when (val result = execute(descriptor)) {
            is ValidationResult.Ok -> {
                if (logger.isDebugEnabled)
                    logger.debug("${action.key} has been executed.")
                ApiResponseV2.Success(version = version, id = descriptor.id)
            }

            is ValidationResult.Error ->
                generateResponseOnFailure(fail = result.reason, version = version, id = descriptor.id, logger = logger)
        }

    abstract fun execute(descriptor: CommandDescriptor): ValidationResult<Fail>
}
