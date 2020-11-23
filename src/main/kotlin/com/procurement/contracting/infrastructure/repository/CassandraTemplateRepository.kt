package com.procurement.contracting.infrastructure.repository

import com.datastax.driver.core.Session
import com.procurement.contracting.application.repository.template.TemplateRepository
import com.procurement.contracting.domain.model.ProcurementMethodDetails
import com.procurement.contracting.infrastructure.extension.cassandra.tryExecute
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess
import org.springframework.stereotype.Repository

@Repository
class CassandraTemplateRepository(private val session: Session) : TemplateRepository {

    companion object {
        private const val FIND_CQL = """
               SELECT ${Database.Template.COLUMN_TEMPLATE}
                 FROM ${Database.KEYSPACE}.${Database.Template.TABLE}
                WHERE ${Database.Template.COLUMN_COUNTRY}=?
                  AND ${Database.Template.COLUMN_PMD}=?
                  AND ${Database.Template.COLUMN_LANGUAGE}=?
                  AND ${Database.Template.COLUMN_TEMPLATE_ID}=?
            """
    }

    private val preparedFindCQL = session.prepare(FIND_CQL)

    override fun findBy(
        country: String,
        pmd: ProcurementMethodDetails,
        language: String,
        templateId: String
    ): Result<String?, Fail.Incident.Database> = preparedFindCQL.bind()
        .apply {
            setString(Database.Template.COLUMN_COUNTRY, country)
            setString(Database.Template.COLUMN_PMD, pmd.key)
            setString(Database.Template.COLUMN_LANGUAGE, language)
            setString(Database.Template.COLUMN_TEMPLATE_ID, templateId)
        }
        .tryExecute(session)
        .onFailure { return it }
        .one()
        ?.getString(Database.Template.COLUMN_TEMPLATE)
        .asSuccess()
}
