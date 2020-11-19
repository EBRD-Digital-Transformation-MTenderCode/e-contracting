package com.procurement.contracting.infrastructure.repository

import com.datastax.driver.core.Session
import com.procurement.contracting.infrastructure.api.command.id.CommandId
import com.procurement.contracting.infrastructure.extension.cassandra.tryExecute
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.HistoryRepository
import com.procurement.contracting.infrastructure.repository.model.HistoryEntity
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess
import com.procurement.contracting.utils.localNowUTC
import com.procurement.contracting.utils.toDate
import com.procurement.contracting.utils.toJson
import org.springframework.stereotype.Repository

@Repository
class HistoryRepositoryCassandra(private val session: Session) : HistoryRepository {

    companion object {
        private const val KEYSPACE = "ocds"
        private const val HISTORY_TABLE = "contracting_history"
        private const val OPERATION_ID = "operation_id"
        private const val COMMAND = "command"
        private const val OPERATION_DATE = "operation_date"
        private const val JSON_DATA = "json_data"

        private const val SAVE_HISTORY_CQL = """
               INSERT INTO $KEYSPACE.$HISTORY_TABLE(
                      $OPERATION_ID,
                      $COMMAND,
                      $OPERATION_DATE,
                      $JSON_DATA
               )
               VALUES(?, ?, ?, ?)
               IF NOT EXISTS
            """

        private const val FIND_HISTORY_ENTRY_CQL = """
               SELECT $OPERATION_ID,
                      $COMMAND,
                      $OPERATION_DATE,
                      $JSON_DATA
                 FROM $KEYSPACE.$HISTORY_TABLE
                WHERE $OPERATION_ID=?
                  AND $COMMAND=?
               LIMIT 1
            """
    }

    private val preparedSaveHistoryCQL = session.prepare(SAVE_HISTORY_CQL)
    private val preparedFindHistoryByCpidAndCommandCQL = session.prepare(FIND_HISTORY_ENTRY_CQL)

    override fun getHistory(commandId: CommandId, command: String): Result<HistoryEntity?, Fail.Incident> {
        val query = preparedFindHistoryByCpidAndCommandCQL.bind()
            .apply {
                setString(OPERATION_ID, commandId.underlying)
                setString(COMMAND, command)
            }

        return query.tryExecute(session)
            .onFailure { return Result.failure(it.reason) }
            .one()
            ?.let { row ->
                HistoryEntity(
                    row.getString(OPERATION_ID),
                    row.getString(COMMAND),
                    row.getTimestamp(OPERATION_DATE),
                    row.getString(JSON_DATA)
                )
            }
            .asSuccess()
    }

    override fun saveHistory(commandId: CommandId, command: String, response: Any): Result<HistoryEntity, Fail.Incident> {
        val entity = HistoryEntity(
            operationId = commandId.underlying,
            command = command,
            operationDate = localNowUTC().toDate(),
            jsonData = toJson(response)
        )

        val insert = preparedSaveHistoryCQL.bind()
            .apply {
                setString(OPERATION_ID, entity.operationId)
                setString(COMMAND, entity.command)
                setTimestamp(OPERATION_DATE, entity.operationDate)
                setString(JSON_DATA, entity.jsonData)
            }

        insert.tryExecute(session)
            .doOnError { error -> return Result.failure(error) }

        return entity.asSuccess()
    }
}
