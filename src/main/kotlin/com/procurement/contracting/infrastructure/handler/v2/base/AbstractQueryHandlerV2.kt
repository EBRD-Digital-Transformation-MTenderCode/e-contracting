package com.procurement.contracting.infrastructure.handler.v2.base

import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.infrastructure.api.v2.ApiResponseV2
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.v2.model.ApiResponseV2Generator.generateResponseOnFailure
import com.procurement.contracting.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.utils.toJson

abstract class AbstractQueryHandlerV2<R : Any>(private val logger: Logger) : AbstractHandlerV2<ApiResponseV2>() {

    override fun handle(descriptor: CommandDescriptor): ApiResponseV2 =
        when (val result = execute(descriptor)) {
            is Result.Success -> {
                if (logger.isDebugEnabled)
                    logger.debug("${action.key} has been executed. Result: ${toJson(result.value)}")
                ApiResponseV2.Success(version = version, id = descriptor.id, result = result.value)
            }

            is Result.Failure ->
                generateResponseOnFailure(fail = result.reason, version = version, id = descriptor.id, logger = logger)
        }

    abstract fun execute(descriptor: CommandDescriptor): Result<R, Fail>
}
