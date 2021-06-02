package com.procurement.contracting.infrastructure.repository

import com.datastax.driver.core.Session
import com.procurement.contracting.application.repository.rule.RuleRepository
import com.procurement.contracting.domain.model.OperationType
import com.procurement.contracting.domain.model.ProcurementMethodDetails
import com.procurement.contracting.infrastructure.extension.cassandra.tryExecute
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.fail.error.RulesError
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess
import org.springframework.stereotype.Repository

@Repository
class CassandraRuleRepository(private val session: Session) : RuleRepository {

    companion object {

        private const val GET_VALUE_BY_CQL = """
               SELECT ${Database.Rules.VALUE}
                 FROM ${Database.KEYSPACE}.${Database.Rules.TABLE}
                WHERE ${Database.Rules.KEY}=? 
                  AND ${Database.Rules.PARAMETER}=?
            """
    }

    private val preparedGetValueByCQL = session.prepare(GET_VALUE_BY_CQL)

    override fun find(
        key: String,
        parameter: String
    ): Result<String?, Fail.Incident.Database.DatabaseInteractionIncident> =
        preparedGetValueByCQL.bind()
            .apply {
                setString(Database.Rules.KEY, key)
                setString(Database.Rules.PARAMETER, parameter)
            }
            .tryExecute(session)
            .onFailure { return it }
            .one()
            ?.getString(Database.Rules.VALUE)
            .asSuccess()

    override fun get(
        key: String,
        parameter: String,
    ): Result<String, Fail> =
        find(key = key, parameter = parameter)
            .onFailure { return it }
            ?.asSuccess()
            ?: RulesError.NotFound(
                description = "Rule '$parameter' by key '$key' is not found."
            ).asFailure()
}
