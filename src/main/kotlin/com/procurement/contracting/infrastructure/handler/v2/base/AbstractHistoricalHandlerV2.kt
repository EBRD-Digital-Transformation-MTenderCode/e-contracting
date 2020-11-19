package com.procurement.contracting.infrastructure.handler.v2.base

import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.application.service.Transform
import com.procurement.contracting.application.service.tryDeserialization
import com.procurement.contracting.infrastructure.api.v2.ApiResponseV2
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.HistoryRepository
import com.procurement.contracting.infrastructure.handler.v2.model.ApiResponseV2Generator.generateResponseOnFailure
import com.procurement.contracting.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.utils.toJson

abstract class AbstractHistoricalHandlerV2<R : Any>(
    private val transform: Transform,
    private val historyRepository: HistoryRepository,
    private val logger: Logger
) : AbstractHandlerV2<ApiResponseV2>() {

    override fun handle(descriptor: CommandDescriptor): ApiResponseV2 {
        val history = historyRepository.getHistory(descriptor.id, action)
            .onFailure {
                return generateResponseOnFailure(
                    fail = it.reason,
                    version = version,
                    id = descriptor.id,
                    logger = logger
                )
            }

        if (history != null) {
            return history.tryDeserialization<ApiResponseV2.Success>(transform)
                .onFailure {
                    return generateResponseOnFailure(
                        fail = Fail.Incident.Transform.ParseFromDatabaseIncident(history, it.reason.exception),
                        id = descriptor.id,
                        version = version,
                        logger = logger
                    )
                }
        }

        return when (val result = execute(descriptor)) {
            is Result.Success -> {
                ApiResponseV2.Success(version = version, id = descriptor.id, result = result.value)
                    .also {
                        historyRepository.saveHistory(descriptor.id, action, toJson(it))
                        if (logger.isDebugEnabled)
                            logger.debug("${action.key} has been executed. Response: ${toJson(it)}")
                    }
            }
            is Result.Failure ->
                generateResponseOnFailure(fail = result.reason, version = version, id = descriptor.id, logger = logger)
        }
    }

    abstract fun execute(descriptor: CommandDescriptor): Result<R, Fail>
}
