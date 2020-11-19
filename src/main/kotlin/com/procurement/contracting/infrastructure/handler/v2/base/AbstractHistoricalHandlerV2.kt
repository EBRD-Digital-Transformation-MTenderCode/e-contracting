package com.procurement.contracting.infrastructure.handler.v2.base

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.infrastructure.api.Action
import com.procurement.contracting.infrastructure.api.ApiVersion
import com.procurement.contracting.infrastructure.api.command.id.CommandId
import com.procurement.contracting.infrastructure.api.v2.ApiResponseV2
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.Handler
import com.procurement.contracting.infrastructure.handler.HistoryRepository
import com.procurement.contracting.infrastructure.handler.v2.generateResponseOnFailure
import com.procurement.contracting.infrastructure.handler.v2.tryGetId
import com.procurement.contracting.infrastructure.handler.v2.tryGetVersion
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.tryToObject

abstract class AbstractHistoricalHandlerV2<ACTION : Action, R : Any>(
    private val target: Class<ApiResponseV2.Success>,
    private val historyRepository: HistoryRepository,
    private val logger: Logger
) : Handler<ACTION, ApiResponseV2> {

    override fun handle(node: JsonNode): ApiResponseV2 {
        val id = node.tryGetId().getOrElse(CommandId.NaN)
        val version = node.tryGetVersion().getOrElse(ApiVersion.NaN)

        val history = historyRepository.getHistory(id, action)
            .onFailure {
                return generateResponseOnFailure(fail = it.reason, version = version, id = id, logger = logger)
            }

        if (history != null) {
            return history.tryToObject(target)
                .onFailure {
                    return generateResponseOnFailure(
                        fail = Fail.Incident.Transform.ParseFromDatabaseIncident(history, it.reason.exception),
                        id = id,
                        version = version,
                        logger = logger
                    )
                }
        }

        return when (val result = execute(node)) {
            is Result.Success -> {
                ApiResponseV2.Success(version = version, id = id, result = result.value)
                    .also {
                        val data = toJson(it)
                        if (logger.isDebugEnabled)
                            logger.debug("${action.key} has been executed. Response: $data")
                        historyRepository.saveHistory(id, action, data)
                    }
            }

            is Result.Failure ->
                generateResponseOnFailure(fail = result.reason, version = version, id = id, logger = logger)
        }
    }

    abstract fun execute(node: JsonNode): Result<R, Fail>
}

