package com.procurement.contracting.application.repository

import com.procurement.contracting.domain.functional.Result
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.model.entity.HistoryEntity
import java.util.*

interface HistoryRepository {
    fun getHistory(operationId: UUID, command: String): Result<HistoryEntity?, Fail.Incident>
    fun saveHistory(operationId: UUID, command: String, response: Any): Result<HistoryEntity, Fail.Incident>
}
