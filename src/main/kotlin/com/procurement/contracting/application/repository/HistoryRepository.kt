package com.procurement.contracting.application.repository

import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.model.entity.HistoryEntity
import com.procurement.contracting.domain.functional.Result

interface HistoryRepository {
    fun getHistory(operationId: String, command: String): Result<HistoryEntity?, Fail.Incident>
    fun saveHistory(operationId: String, command: String, response: Any): Result<HistoryEntity, Fail.Incident>
}
