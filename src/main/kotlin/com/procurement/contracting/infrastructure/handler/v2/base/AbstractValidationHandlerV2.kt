package com.procurement.contracting.infrastructure.handler.v2.base

import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.infrastructure.api.v2.ApiResponseV2
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.v2.model.ApiResponseV2Generator.generateResponseOnFailure
import com.procurement.contracting.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.contracting.lib.functional.ValidationResult
import com.procurement.contracting.utils.toJson


abstract class AbstractValidationHandlerV2(
    private val logger: Logger
) : AbstractHandlerV2<ApiResponseV2>() {

    override fun handle(descriptor: CommandDescriptor): ApiResponseV2 {
        return when (val result = execute(descriptor)) {
            is ValidationResult.Ok -> ApiResponseV2.Success(version = version, id = descriptor.id)
                .also {
                    logger.info("'${action.key}' has been executed. Result: '${toJson(it)}'")
                }
            is ValidationResult.Error -> generateResponseOnFailure(id = descriptor.id, version = version, fail = result.reason, logger = logger)
        }
    }

    abstract fun execute(descriptor: CommandDescriptor): ValidationResult<Fail>
}