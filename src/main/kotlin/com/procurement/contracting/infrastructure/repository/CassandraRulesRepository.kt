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

        private const val ALL_OPERATION_TYPE = "all"

        private const val GET_VALUE_BY_CQL = """
               SELECT ${Database.Rules.VALUE}
                 FROM ${Database.KEYSPACE}.${Database.Rules.TABLE}
                WHERE ${Database.Rules.COUNTRY}=? 
                  AND ${Database.Rules.PMD}=?
                  AND ${Database.Rules.OPERATION_TYPE}=?
                  AND ${Database.Rules.PARAMETER}=?
            """
    }

    private val preparedGetValueByCQL = session.prepare(GET_VALUE_BY_CQL)

    override fun find(
        country: String,
        pmd: ProcurementMethodDetails,
        parameter: String,
        operationType: OperationType?
    ): Result<String?, Fail.Incident.Database.DatabaseInteractionIncident> =
        preparedGetValueByCQL.bind()
            .apply {
                setString(Database.Rules.COUNTRY, country)
                setString(Database.Rules.PMD, pmd.name)
                setString(Database.Rules.OPERATION_TYPE, operationType?.key ?: ALL_OPERATION_TYPE)
                setString(Database.Rules.PARAMETER, parameter)
            }
            .tryExecute(session)
            .onFailure { return it }
            .one()
            ?.getString(Database.Rules.VALUE)
            .asSuccess()

    override fun get(
        country: String,
        pmd: ProcurementMethodDetails,
        parameter: String,
        operationType: OperationType?
    ): Result<String, Fail> =
        find(country = country, pmd = pmd, operationType = operationType, parameter = parameter)
            .onFailure { return it }
            ?.asSuccess()
            ?: RulesError.NotFound(
                description = "Rule '$parameter' by country '$country' and pmd '${pmd.key}' and operation type '${operationType?.key ?: ALL_OPERATION_TYPE}' is not found."
            ).asFailure()
}
