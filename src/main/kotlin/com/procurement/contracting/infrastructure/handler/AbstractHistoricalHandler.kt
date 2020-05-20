package com.procurement.contracting.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.contracting.application.repository.HistoryRepository
import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.domain.functional.Result
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.web.dto.Action
import com.procurement.contracting.infrastructure.web.dto.ApiResponse2
import com.procurement.contracting.infrastructure.web.dto.ApiSuccessResponse2
import com.procurement.contracting.model.dto.bpe.generateResponseOnFailure
import com.procurement.contracting.model.dto.bpe.tryGetId
import com.procurement.contracting.model.dto.bpe.tryGetVersion
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.tryToObject

abstract class AbstractHistoricalHandler<ACTION : Action, R : Any>(
    private val target: Class<ApiSuccessResponse2>,
    private val historyRepository: HistoryRepository,
    private val logger: Logger
) : Handler<ACTION, ApiResponse2> {

    override fun handle(node: JsonNode): ApiResponse2 {
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
                ApiSuccessResponse2(version = version, id = id, result = result.get).also {
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

