package com.procurement.contracting.infrastructure.handler

import com.procurement.contracting.infrastructure.api.Action
import com.procurement.contracting.infrastructure.api.command.id.CommandId
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result

interface HistoryRepository {
    fun getHistory(commandId: CommandId, action: Action): Result<String?, Fail.Incident.Database>
    fun saveHistory(commandId: CommandId, action: Action, data: String): Result<Boolean, Fail.Incident.Database>
}
