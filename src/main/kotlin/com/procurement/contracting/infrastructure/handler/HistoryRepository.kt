package com.procurement.contracting.infrastructure.handler

import com.procurement.contracting.domain.functional.Result
import com.procurement.contracting.infrastructure.api.command.id.CommandId
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.repository.model.HistoryEntity

interface HistoryRepository {
    fun getHistory(commandId: CommandId, command: String): Result<HistoryEntity?, Fail.Incident>
    fun saveHistory(commandId: CommandId, command: String, response: Any): Result<HistoryEntity, Fail.Incident>
}
