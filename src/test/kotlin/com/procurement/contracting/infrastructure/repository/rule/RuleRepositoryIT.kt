package com.procurement.contracting.infrastructure.repository.rule

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
import com.procurement.contracting.application.repository.rule.RuleRepository
import com.procurement.contracting.domain.model.OperationType
import com.procurement.contracting.domain.model.ProcurementMethodDetails
import com.procurement.contracting.get
import com.procurement.contracting.infrastructure.repository.CassandraRuleRepository
import com.procurement.contracting.infrastructure.repository.CassandraTestContainer
import com.procurement.contracting.infrastructure.repository.Database
import com.procurement.contracting.infrastructure.repository.DatabaseTestConfiguration
import com.procurement.contracting.lib.functional.Result
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DatabaseTestConfiguration::class])
class RuleRepositoryIT {
    companion object {
        private const val PARAMETER = "someParameter"
        private const val COUNTRY = "MD"
        private val PMD = ProcurementMethodDetails.CF
        private val OPERATION_TYPE = OperationType.WITHDRAW_QUALIFICATION_PROTOCOL
    }

    @Autowired
    private lateinit var container: CassandraTestContainer

    private lateinit var session: Session
    private lateinit var ruleRepository: RuleRepository

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

        ruleRepository = CassandraRuleRepository(session)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun find_success() {
        val value = "10"
        insertRule(value)
        val actual = ruleRepository.find(COUNTRY, PMD, PARAMETER, OPERATION_TYPE).get()

        assertEquals(value, actual)
    }

    @Test
    fun find_noValueFound_success() {
        val actual = ruleRepository.find(COUNTRY, PMD, PARAMETER, OPERATION_TYPE).get()

        assertTrue(actual == null)
    }

    @Test
    fun find_fail() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val expected = ruleRepository.find(COUNTRY, PMD, PARAMETER, OPERATION_TYPE)

        assertTrue(expected is Result.Failure)
    }

    private fun createKeyspace() {
        session.execute(
            "CREATE KEYSPACE ${Database.KEYSPACE} " +
                "WITH replication = {'class' : 'SimpleStrategy', 'replication_factor' : 1};"
        )
    }

    private fun dropKeyspace() {
        session.execute("DROP KEYSPACE ${Database.KEYSPACE};")
    }

    private fun createTable() {
        session.execute(
            """
                CREATE TABLE IF NOT EXISTS ${Database.KEYSPACE}.${Database.Rules.TABLE}
                    (
                        ${Database.Rules.COUNTRY}        TEXT,
                        ${Database.Rules.PMD}            TEXT,
                        ${Database.Rules.OPERATION_TYPE} TEXT,
                        ${Database.Rules.PARAMETER}      TEXT,
                        ${Database.Rules.VALUE}          TEXT,
                        PRIMARY KEY (${Database.Rules.COUNTRY}, ${Database.Rules.PMD}, ${Database.Rules.OPERATION_TYPE}, ${Database.Rules.PARAMETER})
                    );
            """
        )
    }

    private fun insertRule(value: String) {
        val record = QueryBuilder.insertInto(Database.KEYSPACE, Database.Rules.TABLE)
            .value(Database.Rules.COUNTRY, COUNTRY)
            .value(Database.Rules.PMD, PMD.key)
            .value(Database.Rules.OPERATION_TYPE, OPERATION_TYPE.key)
            .value(Database.Rules.PARAMETER, PARAMETER)
            .value(Database.Rules.VALUE, value)

        session.execute(record)
    }
}
