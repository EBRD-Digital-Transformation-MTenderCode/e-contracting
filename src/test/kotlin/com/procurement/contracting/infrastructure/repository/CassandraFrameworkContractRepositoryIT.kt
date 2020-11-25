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
import com.procurement.contracting.application.repository.fc.FrameworkContractRepository
import com.procurement.contracting.application.repository.fc.model.FrameworkContractEntity
import com.procurement.contracting.assertFailure
import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.fc.id.FrameworkContractId
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatus
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatusDetails
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
class CassandraFrameworkContractRepositoryIT {

    companion object {
        private val CPID = Cpid.orNull("ocds-b3wdp1-MD-1580458690892")!!
        private val OCID = Ocid.orNull("ocds-b3wdp1-MD-1580458690892-AC-1580123456789")!!
        private val FC_ID = FrameworkContractId.generate()
        private val TOKEN = Token.generate()
        private val OWNER = Owner.orNull("d0da4c24-1a2a-4b39-a1fd-034cb887c93b")!!
        private val CREATE_DATE = LocalDateTime.now()
        private val FC_STATUS = FrameworkContractStatus.PENDING
        private val UPDATED_FC_STATUS = FrameworkContractStatus.CANCELLED
        private val FC_STATUS_DETAILS = FrameworkContractStatusDetails.CONTRACT_PROJECT
        private val UPDATED_FC_STATUS_DETAILS = FrameworkContractStatusDetails.WITHDRAWN_QUALIFICATION_PROTOCOL
        private const val JSON_DATA = """ {"fc": "data"} """
        private const val UPDATED_JSON_DATA = """ {"updated fc": "data"} """
    }

    @Autowired
    private lateinit var container: CassandraTestContainer

    private lateinit var session: Session
    private lateinit var fcRepository: FrameworkContractRepository

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

        fcRepository = CassandraFrameworkContractRepository(session)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun findBy() {
        insertFC()

        val actualFundedAC: FrameworkContractEntity? =
            fcRepository.findBy(cpid = CPID, ocid = OCID, contractId = FC_ID).get()

        assertNotNull(actualFundedAC)

        val expected = expectedFC(
            status = FC_STATUS,
            statusDetails = FC_STATUS_DETAILS,
            jsonData = JSON_DATA
        )
        assertEquals(expected, actualFundedAC)
    }

    @Test
    fun fcNotFound() {
        val actualFundedAC =
            fcRepository.findBy(cpid = CPID, ocid = OCID, contractId = FC_ID).get()
        assertNull(actualFundedAC)
    }

    @Test
    fun errorRead() {
        insertFC()

        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        fcRepository.findBy(cpid = CPID, ocid = OCID, contractId = FC_ID).assertFailure()
    }

    @Test
    fun findById() {
        insertFC()

        val actualFundedAC: List<FrameworkContractEntity> = fcRepository.findBy(cpid = CPID, ocid = OCID).get()

        assertTrue(actualFundedAC.size == 1)

        val expected = expectedFC(
            status = FC_STATUS,
            statusDetails = FC_STATUS_DETAILS,
            jsonData = JSON_DATA
        )
        assertEquals(expected, actualFundedAC[0])
    }

    @Test
    fun fcByIdNotFound() {
        val actualFundedAC =
            fcRepository.findBy(cpid = CPID, ocid = OCID).get()
        assertTrue(actualFundedAC.isEmpty())
    }

    @Test
    fun errorReadById() {
        insertFC()

        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        fcRepository.findBy(cpid = CPID, ocid = OCID).assertFailure()
    }

    @Test
    fun saveNew() {
        val entity = expectedFC(
            status = FC_STATUS,
            statusDetails = FC_STATUS_DETAILS,
            jsonData = JSON_DATA
        )

        fcRepository.saveNew(entity)

        val actualACEntity: FrameworkContractEntity? =
            fcRepository.findBy(cpid = CPID, ocid = OCID, contractId = FC_ID).get()

        assertNotNull(actualACEntity)
        assertEquals(entity, actualACEntity)
    }

    @Test
    fun errorAlreadyAC() {
        val entity = expectedFC(
            status = FC_STATUS,
            statusDetails = FC_STATUS_DETAILS,
            jsonData = JSON_DATA
        )

        fcRepository.saveNew(entity)

        val wasApplied = fcRepository.saveNew(entity).get()
        assertFalse(wasApplied)
    }

    @Test
    fun errorSaveNew() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val entity = expectedFC(
            status = FC_STATUS,
            statusDetails = FC_STATUS_DETAILS,
            jsonData = JSON_DATA
        )

        val exception = assertThrows<SaveEntityException> {
            fcRepository.saveNew(entity).orThrow { it.exception }
        }
        assertEquals("Error writing new FC contract to database.", exception.message)
    }

    @Test
    fun update() {
        val fcEntity = expectedFC(
            status = FC_STATUS,
            statusDetails = FC_STATUS_DETAILS,
            jsonData = JSON_DATA
        )
        val wasSaved = fcRepository.saveNew(fcEntity).get()
        assertTrue(wasSaved)

        val updatedEntity = fcEntity.copy(
            status = UPDATED_FC_STATUS,
            statusDetails = UPDATED_FC_STATUS_DETAILS,
            jsonData = UPDATED_JSON_DATA
        )
        val wasUpdated = fcRepository.update(updatedEntity).get()
        assertTrue(wasUpdated)

        val actualEntity = fcRepository.findBy(cpid = CPID, ocid = OCID, contractId = FC_ID).get()

        assertNotNull(actualEntity)
        assertEquals(updatedEntity, actualEntity)
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
                CREATE TABLE IF NOT EXISTS  ocds.contracting_fc (
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

    private fun insertFC(
        status: FrameworkContractStatus = FC_STATUS,
        statusDetails: FrameworkContractStatusDetails = FC_STATUS_DETAILS,
        jsonData: String = JSON_DATA
    ) {
        val rec = QueryBuilder.insertInto("ocds", "contracting_fc")
            .value("cpid", CPID.underlying)
            .value("ocid", OCID.underlying)
            .value("id", FC_ID.underlying)
            .value("token_entity", TOKEN.underlying.toString())
            .value("owner", OWNER.underlying)
            .value("created_date", CREATE_DATE.toCassandraTimestamp())
            .value("status", status.key)
            .value("status_details", statusDetails.key)
            .value("json_data", jsonData)
        session.execute(rec)
    }

    private fun expectedFC(
        status: FrameworkContractStatus,
        statusDetails: FrameworkContractStatusDetails,
        jsonData: String
    ) = FrameworkContractEntity(
        cpid = CPID,
        ocid = OCID,
        id = FC_ID,
        token = TOKEN,
        owner = OWNER,
        createdDate = CREATE_DATE,
        status = status,
        statusDetails = statusDetails,
        jsonData = jsonData
    )
}
