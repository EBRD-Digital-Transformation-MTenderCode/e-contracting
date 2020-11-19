package com.procurement.contracting.infrastructure.handler.v2.base

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.domain.functional.Result
import com.procurement.contracting.infrastructure.api.Action
import com.procurement.contracting.infrastructure.api.v2.ApiResponseV2
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.Handler
import com.procurement.contracting.infrastructure.handler.HistoryRepository
import com.procurement.contracting.infrastructure.handler.v2.generateResponseOnFailure
import com.procurement.contracting.infrastructure.handler.v2.tryGetId
import com.procurement.contracting.infrastructure.handler.v2.tryGetVersion
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.tryToObject

abstract class AbstractHistoricalHandler<ACTION : Action, R : Any>(
    private val target: Class<ApiResponseV2.Success>,
    private val historyRepository: HistoryRepository,
    private val logger: Logger
) : Handler<ACTION, ApiResponseV2> {

    override fun handle(node: JsonNode): ApiResponseV2 {
        val id = node.tryGetId().get
        val version = node.tryGetVersion().get

        val history = historyRepository.getHistory(id, action.key)
            .doOnError { error ->
                return generateResponseOnFailure(
                    fail = error,
                    version = version,
                    id = id,
                    logger = logger
                )
            }
            .get
        if (history != null) {
            val data = history.jsonData
            return data.tryToObject(target)
                .doReturn { error ->
                    return generateResponseOnFailure(
                        fail = Fail.Incident.Transform.ParseFromDatabaseIncident(data, error.exception),
                        id = id,
                        version = version,
                        logger = logger
                    )
                }
        }

        return when (val result = execute(node)) {
            is Result.Success -> {
                ApiResponseV2.Success(version = version, id = id, result = result.get).also {
                    historyRepository.saveHistory(id, action.key, it)
                    if (logger.isDebugEnabled)
                        logger.debug("${action.key} has been executed. Response: ${toJson(it)}")
                }
            }
            is Result.Failure -> generateResponseOnFailure(
                fail = result.error,
                version = version,
                id = id,
                logger = logger
            )
        }
    }

    abstract fun execute(node: JsonNode): Result<R, Fail>
}

