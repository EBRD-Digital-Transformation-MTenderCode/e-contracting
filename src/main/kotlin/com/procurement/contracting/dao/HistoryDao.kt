package com.procurement.contracting.dao

import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.QueryBuilder.eq
import com.datastax.driver.core.querybuilder.QueryBuilder.insertInto
import com.datastax.driver.core.querybuilder.QueryBuilder.select
import com.procurement.contracting.infrastructure.api.command.id.CommandId
import com.procurement.contracting.infrastructure.api.v1.ApiResponseV1
import com.procurement.contracting.infrastructure.repository.model.HistoryEntity
import com.procurement.contracting.utils.localNowUTC
import com.procurement.contracting.utils.toDate
import com.procurement.contracting.utils.toJson
import org.springframework.stereotype.Service

@Service
class HistoryDao(private val session: Session) {

    fun getHistory(commandId: CommandId, command: String): HistoryEntity? {
        val query = select()
                .all()
                .from(HISTORY_TABLE)
                .where(eq(OPERATION_ID, commandId.underlying))
                .and(eq(COMMAND, command))
                .limit(1)
        val row = session.execute(query).one()
        return if (row != null) HistoryEntity(
                row.getString(OPERATION_ID),
                row.getString(COMMAND),
                row.getTimestamp(OPERATION_DATE),
                row.getString(JSON_DATA)) else null
    }

    fun saveHistory(commandId: CommandId, command: String, response: ApiResponseV1.Success): HistoryEntity {
        val entity = HistoryEntity(
                operationId = commandId.underlying,
                command = command,
                operationDate = localNowUTC().toDate(),
                jsonData = toJson(response))

        val insert = insertInto(HISTORY_TABLE)
                .value(OPERATION_ID, entity.operationId)
                .value(COMMAND, entity.command)
                .value(OPERATION_DATE, entity.operationDate)
                .value(JSON_DATA, entity.jsonData)
        session.execute(insert)
        return entity
    }

    companion object {
        private const val HISTORY_TABLE = "contracting_history"
        private const val OPERATION_ID = "operation_id"
        private const val COMMAND = "command"
        private const val OPERATION_DATE = "operation_date"
        private const val JSON_DATA = "json_data"
    }

}
