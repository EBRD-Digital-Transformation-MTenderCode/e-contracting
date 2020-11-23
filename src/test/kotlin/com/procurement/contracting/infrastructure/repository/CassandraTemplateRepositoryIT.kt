package com.procurement.contracting.infrastructure.repository

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.HostDistance
import com.datastax.driver.core.PlainTextAuthProvider
import com.datastax.driver.core.PoolingOptions
import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doThrow
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.contracting.application.repository.template.TemplateRepository
import com.procurement.contracting.domain.model.ProcurementMethodDetails
import com.procurement.contracting.failure
import com.procurement.contracting.get
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DatabaseTestConfiguration::class])
class CassandraTemplateRepositoryIT {

    companion object {
        private const val COUNTRY = "MD"
        private val PMD = ProcurementMethodDetails.CD
        private const val LANGUAGE: String = "RO"
        private const val TEMPLATE_ID: String = "template-1"
        private const val TEMPLATE: String = "Template"
    }

    @Autowired
    private lateinit var container: CassandraTestContainer
    private lateinit var session: Session
    private lateinit var repository: TemplateRepository

    @BeforeEach
    fun init() {
        val poolingOptions = PoolingOptions()
            .setMaxConnectionsPerHost(HostDistance.LOCAL, 1)
        val cluster = Cluster.builder()
            .addContactPoints(container.contractPoint)
            .withPort(container.port)
            .withoutJMXReporting()
            .withPoolingOptions(poolingOptions)
            .withAuthProvider(PlainTextAuthProvider(container.username, container.password))
            .build()

        session = spy(cluster.connect())

        createKeyspace()
        createTable()

        repository = CassandraTemplateRepository(session)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun findTemplate() {
        insert()

        val template =
            repository.findBy(country = COUNTRY, pmd = PMD, language = LANGUAGE, templateId = TEMPLATE_ID).get()
        assertNotNull(template)
        assertEquals(TEMPLATE, template)
    }

    @Test
    fun templateNotFound() {
        val template =
            repository.findBy(country = COUNTRY, pmd = PMD, language = LANGUAGE, templateId = TEMPLATE_ID).get()
        assertNull(template)
    }

    @Test
    fun findErrorOfDatabase() {
        insert()

        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        repository.findBy(country = COUNTRY, pmd = PMD, language = LANGUAGE, templateId = TEMPLATE_ID).failure()
    }

    private fun createKeyspace() {
        session.execute(
            "CREATE KEYSPACE ocds " +
                "WITH replication = {'class' : 'SimpleStrategy', 'replication_factor' : 1};"
        )
    }

    private fun dropKeyspace() {
        session.execute("DROP KEYSPACE ocds;")
    }

    private fun createTable() {
        session.execute(
            """
                CREATE TABLE IF NOT EXISTS ocds.contracting_templates
                    (
                        country     TEXT,
                        pmd         TEXT,
                        language    TEXT,
                        template_id TEXT,
                        template    TEXT,
                        PRIMARY KEY(country, pmd, language, template_id)
                    );
            """
        )
    }

    private fun insert() {
        val query = QueryBuilder.insertInto("ocds", "contracting_templates")
            .value("country", COUNTRY)
            .value("pmd", PMD.key)
            .value("template_id", TEMPLATE_ID)
            .value("language", LANGUAGE)
            .value("template", TEMPLATE)
        session.execute(query)
    }
}
