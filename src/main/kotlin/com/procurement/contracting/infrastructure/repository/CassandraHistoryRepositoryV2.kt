package com.procurement.contracting.infrastructure.repository

import com.datastax.driver.core.Session
import com.procurement.contracting.domain.util.extension.nowDefaultUTC
import com.procurement.contracting.infrastructure.api.Action
import com.procurement.contracting.infrastructure.api.command.id.CommandId
import com.procurement.contracting.infrastructure.extension.cassandra.toCassandraTimestamp
import com.procurement.contracting.infrastructure.extension.cassandra.tryExecute
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.HistoryRepository
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Repository
@Primary
@Qualifier("contracting")
class CassandraHistoryRepositoryV2(@Qualifier("contracting") private val session: Session) : HistoryRepository {

    companion object {

        private const val SAVE_HISTORY_CQL = """
               INSERT INTO ${Database.KEYSPACE_CONTRACTING}.${Database.History_V2.TABLE}(
                      ${Database.History_V2.COMMAND_ID},
                      ${Database.History_V2.COMMAND_NAME},
                      ${Database.History_V2.COMMAND_DATE},
                      ${Database.History_V2.JSON_DATA}
               )
               VALUES(?, ?, ?, ?)
               IF NOT EXISTS
            """

        private const val FIND_HISTORY_ENTRY_CQL = """
               SELECT ${Database.History_V2.COMMAND_ID},
                      ${Database.History_V2.COMMAND_NAME},
                      ${Database.History_V2.COMMAND_DATE},
                      ${Database.History_V2.JSON_DATA}
                 FROM ${Database.KEYSPACE_CONTRACTING}.${Database.History_V2.TABLE}
                WHERE ${Database.History_V2.COMMAND_ID}=?
                  AND ${Database.History_V2.COMMAND_NAME}=?
            """
    }

    private val preparedSaveHistoryCQL = session.prepare(SAVE_HISTORY_CQL)
    private val preparedFindHistoryByCpidAndCommandCQL = session.prepare(FIND_HISTORY_ENTRY_CQL)

    override fun getHistory(commandId: CommandId, action: Action): Result<String?, Fail.Incident.Database> =
        preparedFindHistoryByCpidAndCommandCQL.bind()
            .apply {
                setString(Database.History_V2.COMMAND_ID, commandId.underlying)
                setString(Database.History_V2.COMMAND_NAME, action.key)
            }
            .tryExecute(session)
            .onFailure { return it }
            .one()
            ?.getString(Database.History_V2.JSON_DATA)
            .asSuccess()

    override fun saveHistory(
        commandId: CommandId,
        action: Action,
        data: String
    ): Result<Boolean, Fail.Incident.Database> = preparedSaveHistoryCQL.bind()
        .apply {
            setString(Database.History_V2.COMMAND_ID, commandId.underlying)
            setString(Database.History_V2.COMMAND_NAME, action.key)
            setTimestamp(Database.History_V2.COMMAND_DATE, nowDefaultUTC().toCassandraTimestamp())
            setString(Database.History_V2.JSON_DATA, data)
        }
        .tryExecute(session)
        .onFailure { return it }
        .wasApplied()
        .asSuccess()
}
