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
import com.procurement.contracting.application.exception.repository.SaveEntityException
import com.procurement.contracting.application.repository.pac.model.PacEntity
import com.procurement.contracting.assertFailure
import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.pac.PacStatus
import com.procurement.contracting.domain.model.pac.PacStatusDetails
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.get
import com.procurement.contracting.infrastructure.extension.cassandra.toCassandraTimestamp
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DatabaseTestConfiguration::class])
class CassandraPacRepositoryIT {

    companion object {
        private val CPID = Cpid.orNull("ocds-b3wdp1-MD-1580458690892")!!
        private val OCID = Ocid.orNull("ocds-b3wdp1-MD-1580458690892-AC-1580123456789")!!
        private val PAC_ID = PacId.generate()
        private val TOKEN = Token.generate()
        private val OWNER = Owner.orNull("d0da4c24-1a2a-4b39-a1fd-034cb887c93b")!!
        private val CREATE_DATE = LocalDateTime.now()
        private val PAC_STATUS = PacStatus.PENDING
        private val PAC_STATUS_DETAILS = PacStatusDetails.CONCLUDED
        private val UPDATED_PAC_STATUS_DETAILS = PacStatusDetails.ALL_REJECTED
        private const val JSON_DATA = """ {"pac": "data"} """
        private const val UPDATED_JSON_DATA = """ {"updated pac": "data"} """
    }

    @Autowired
    private lateinit var container: CassandraTestContainer

    private lateinit var session: Session
    private lateinit var pacRepository: CassandraPacRepository

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

        pacRepository = CassandraPacRepository(session)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun findBy() {
        insertPac()

        val actualPac: PacEntity? =
            pacRepository.findBy(cpid = CPID, ocid = OCID, contractId = PAC_ID).get()

        val expected = expectedPac()
        assertEquals(expected, actualPac)
    }

    @Test
    fun findByWithNullableToken() {
        insertPac(token = null)

        val actualPac: PacEntity? =
            pacRepository.findBy(cpid = CPID, ocid = OCID, contractId = PAC_ID).get()

        val expected = expectedPac(token = null)
        assertEquals(expected, actualPac)
    }

    @Test
    fun fcNotFound() {
        val actual = pacRepository.findBy(cpid = CPID, ocid = OCID, contractId = PAC_ID).get()
        assertNull(actual)
    }

    @Test
    fun errorRead() {
        insertPac()

        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        pacRepository.findBy(cpid = CPID, ocid = OCID, contractId = PAC_ID).assertFailure()
    }

    @Test
    fun findById() {
        insertPac()

        val actual = pacRepository.findBy(cpid = CPID, ocid = OCID).get()

        assertTrue(actual.size == 1)

        val expected = expectedPac()
        assertEquals(expected, actual[0])
    }

    @Test
    fun pacByIdNotFound() {
        val actual = pacRepository.findBy(cpid = CPID, ocid = OCID).get()
        assertTrue(actual.isEmpty())
    }

    @Test
    fun errorReadById() {
        insertPac()

        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        pacRepository.findBy(cpid = CPID, ocid = OCID).assertFailure()
    }

    @Test
    fun saveNew() {
        val entity = expectedPac()

        pacRepository.saveNew(entity)

        val actual = pacRepository.findBy(cpid = CPID, ocid = OCID, contractId = PAC_ID).get()
        assertEquals(entity, actual)
    }

    @Test
    fun saveNewWithNullableToken() {
        val entity = expectedPac(token = null)

        pacRepository.saveNew(entity)

        val actual = pacRepository.findBy(cpid = CPID, ocid = OCID, contractId = PAC_ID).get()
        assertEquals(entity, actual)
    }

    @Test
    fun errorAlreadySaved() {
        val entity = expectedPac()

        pacRepository.saveNew(entity)

        val wasApplied = pacRepository.saveNew(entity).get()
        assertFalse(wasApplied)
    }

    @Test
    fun errorSaveNew() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val entity = expectedPac()

        val exception = assertThrows<SaveEntityException> {
            pacRepository.saveNew(entity).orThrow { it.exception }
        }
        assertEquals("Error writing new PAC contract to database.", exception.message)
    }

    @Test
    fun update() {
        val fcEntity = expectedPac()
        val wasSaved = pacRepository.saveNew(fcEntity).get()
        assertTrue(wasSaved)

        val updatedEntity = fcEntity.copy(
            status = PAC_STATUS,
            statusDetails = UPDATED_PAC_STATUS_DETAILS,
            jsonData = UPDATED_JSON_DATA
        )
        val wasUpdated = pacRepository.update(updatedEntity).get()
        assertTrue(wasUpdated)

        val actualEntity = pacRepository.findBy(cpid = CPID, ocid = OCID, contractId = PAC_ID).get()

        assertNotNull(actualEntity)
        assertEquals(updatedEntity, actualEntity)
    }

    @Test
    fun saveAll() {
        val firstEntity = expectedPac()
        val secondEntity = firstEntity.copy(id = PacId.generate())
        val expected = setOf(firstEntity, secondEntity)

        pacRepository.save(expected)

        val actual = pacRepository.findBy(cpid = CPID, ocid = OCID).get()

        assertTrue(actual.size == 2)
        assertEquals(expected, actual.toSet())
    }

    private fun createKeyspace() {
        session.execute("CREATE KEYSPACE ocds WITH replication = {'class' : 'SimpleStrategy', 'replication_factor' : 1};")
    }

    private fun dropKeyspace() {
        session.execute("DROP KEYSPACE ocds;")
    }

    private fun createTable() {
        session.execute(
            """
                CREATE TABLE IF NOT EXISTS  ocds.contracting_pac (
                    cpid           TEXT,
                    ocid           TEXT,
                    id             TEXT,
                    token_entity   TEXT,
                    owner          TEXT,
                    created_date   TIMESTAMP,
                    status         TEXT,
                    status_details TEXT,
                    json_data      TEXT,
                    PRIMARY KEY(cpid, ocid, id)
                );
            """
        )
    }

    private fun insertPac(
        status: PacStatus = PAC_STATUS,
        statusDetails: PacStatusDetails = PAC_STATUS_DETAILS,
        jsonData: String = JSON_DATA,
        token: Token? = TOKEN
    ) {
        val rec = QueryBuilder.insertInto("ocds", "contracting_pac")
            .value("cpid", CPID.underlying)
            .value("ocid", OCID.underlying)
            .value("id", PAC_ID.underlying)
            .value("token_entity", token?.toString())
            .value("owner", OWNER.underlying)
            .value("created_date", CREATE_DATE.toCassandraTimestamp())
            .value("status", status.key)
            .value("status_details", statusDetails.key)
            .value("json_data", jsonData)
        session.execute(rec)
    }

    private fun expectedPac(
        status: PacStatus = PAC_STATUS,
        statusDetails: PacStatusDetails = PAC_STATUS_DETAILS,
        jsonData: String = JSON_DATA,
        token: Token? = TOKEN
    ) = PacEntity(
        cpid = CPID,
        ocid = OCID,
        id = PAC_ID,
        token = token,
        owner = OWNER,
        createdDate = CREATE_DATE,
        status = status,
        statusDetails = statusDetails,
        jsonData = jsonData
    )
}
