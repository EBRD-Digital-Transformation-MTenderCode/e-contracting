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
import com.procurement.contracting.application.exception.repository.ReadEntityException
import com.procurement.contracting.application.exception.repository.SaveEntityException
import com.procurement.contracting.application.repository.ACRepository
import com.procurement.contracting.domain.entity.ACEntity
import com.procurement.contracting.domain.model.MainProcurementCategory
import com.procurement.contracting.domain.model.contract.status.ContractStatus
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime
import java.util.*

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DatabaseTestConfiguration::class])
class CassandraACRepositoryIT {
    companion object {
        private const val CPID = "cpid-1"
        private const val CONTRACT_ID = "contract-id"
        private val TOKEN = UUID.randomUUID()
        private const val OWNER = "owner-1"
        private val CREATE_DATE = LocalDateTime.now()
        private val AC_STATUS = ContractStatus.PENDING
        private val AC_STATUS_AFTER_CANCEL = ContractStatus.CANCELLED
        private val AC_STATUS_AFTER_UPDATE = ContractStatus.UNSUCCESSFUL
        private val AC_STATUS_DETAILS = ContractStatusDetails.VERIFIED
        private val AC_STATUS_DETAILS_AFTER_CANCEL = ContractStatusDetails.EMPTY
        private val AC_STATUS_DETAILS_AFTER_UPDATE = ContractStatusDetails.EMPTY
        private val MPC = MainProcurementCategory.SERVICES
        private const val LANGUAGE = "ro"
        private const val JSON_DATA = """ {"ac": "data"} """
        private const val JSON_DATA_CANCELLED_AC = """ {"ac": "canceled data"} """
        private const val JSON_DATA_UPDATED_AC = """ {"status": "updated"} """
    }

    @Autowired
    private lateinit var container: CassandraTestContainer

    private lateinit var session: Session
    private lateinit var acRepository: ACRepository

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

        acRepository = CassandraACRepository(session)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun findBy() {
        insertAC()

        val actualFundedAC: ACEntity? = acRepository.findBy(cpid = CPID, contractId = CONTRACT_ID)

        assertNotNull(actualFundedAC)
        assertEquals(expectedFundedAC(), actualFundedAC)
    }

    @Test
    fun acNotFound() {
        val actualFundedAC = acRepository.findBy(cpid = "UNKNOWN", contractId = CONTRACT_ID)
        assertNull(actualFundedAC)
    }

    @Test
    fun errorRead() {
        insertAC()

        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val exception = assertThrows<ReadEntityException> {
            acRepository.findBy(cpid = CPID, contractId = CONTRACT_ID)
        }
        assertEquals("Error read Contract(s) from the database.", exception.message)
    }

    @Test
    fun cancellationAC() {
        insertAC()

        acRepository.saveCancelledAC(
            cpid = CPID,
            id = CONTRACT_ID,
            status = AC_STATUS_AFTER_CANCEL,
            statusDetails = AC_STATUS_DETAILS_AFTER_CANCEL,
            jsonData = JSON_DATA_CANCELLED_AC
        )

        val actualFundedAC: ACEntity? = acRepository.findBy(cpid = CPID, contractId = CONTRACT_ID)

        val expectedACEntity = expectedCancelledAC()

        assertNotNull(actualFundedAC)
        assertEquals(expectedACEntity, actualFundedAC)
    }

    @Test
    fun errorSaveCancelledAC() {
        insertAC()

        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val exception = assertThrows<SaveEntityException> {
            acRepository.saveCancelledAC(
                cpid = CPID,
                id = CONTRACT_ID,
                status = AC_STATUS_AFTER_CANCEL,
                statusDetails = AC_STATUS_DETAILS_AFTER_CANCEL,
                jsonData = JSON_DATA_CANCELLED_AC
            )
        }
        assertEquals("Error writing cancelled contract.", exception.message)
    }

    @Test
    fun updateStatusesAC() {
        insertAC()

        acRepository.updateStatusesAC(
            cpid = CPID,
            id = CONTRACT_ID,
            status = AC_STATUS_AFTER_UPDATE,
            statusDetails = AC_STATUS_DETAILS_AFTER_UPDATE,
            jsonData = JSON_DATA_UPDATED_AC
        )

        val actualACEntity: ACEntity? = acRepository.findBy(cpid = CPID, contractId = CONTRACT_ID)

        val expectedACEntity = expectedUpdatedAC()

        assertNotNull(actualACEntity)
        assertEquals(expectedACEntity, actualACEntity)
    }

    @Test
    fun errorSaveUpdatedAC() {
        insertAC()

        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val exception = assertThrows<SaveEntityException> {
            acRepository.updateStatusesAC(
                cpid = CPID,
                id = CONTRACT_ID,
                status = AC_STATUS_AFTER_UPDATE,
                statusDetails = AC_STATUS_DETAILS_AFTER_UPDATE,
                jsonData = JSON_DATA_UPDATED_AC
            )
        }
        assertEquals("Error writing updated contract.", exception.message)
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
                CREATE TABLE IF NOT EXISTS  ocds.contracting_ac (
                    cp_id text,
                    ac_id text,
                    token_entity UUID,
                    owner text,
                    created_date timestamp,
                    status text,
                    status_details text,
                    mpc text,
                    language text,
                    json_data text,
                    PRIMARY KEY(cp_id, ac_id)
                );
            """
        )
    }

    private fun insertAC(
        status: ContractStatus = AC_STATUS,
        statusDetails: ContractStatusDetails = AC_STATUS_DETAILS,
        jsonData: String = JSON_DATA
    ) {
        val rec = QueryBuilder.insertInto("ocds", "contracting_ac")
            .value("cp_id", CPID)
            .value("ac_id", CONTRACT_ID)
            .value("created_date", CREATE_DATE.toCassandraTimestamp())
            .value("json_data", jsonData)
            .value("language", LANGUAGE)
            .value("mpc", MPC.toString())
            .value("owner", OWNER)
            .value("status", status.toString())
            .value("status_details", statusDetails.toString())
            .value("token_entity", TOKEN)
        session.execute(rec)
    }

    private fun expectedFundedAC() = ACEntity(
        cpid = CPID,
        id = CONTRACT_ID,
        token = TOKEN,
        owner = OWNER,
        createdDate = CREATE_DATE,
        status = AC_STATUS.toString(),
        statusDetails = AC_STATUS_DETAILS.toString(),
        mainProcurementCategory = MPC.toString(),
        language = LANGUAGE,
        jsonData = JSON_DATA
    )

    private fun expectedCancelledAC() = expectedAC(
        status = AC_STATUS_AFTER_CANCEL,
        statusDetails = AC_STATUS_DETAILS_AFTER_CANCEL,
        jsonData = JSON_DATA_CANCELLED_AC
    )

    private fun expectedUpdatedAC() = expectedAC(
        status = AC_STATUS_AFTER_UPDATE,
        statusDetails = AC_STATUS_DETAILS_AFTER_UPDATE,
        jsonData = JSON_DATA_UPDATED_AC
    )

    private fun expectedAC(
        status: ContractStatus,
        statusDetails: ContractStatusDetails,
        jsonData: String
    ) = ACEntity(
        cpid = CPID,
        id = CONTRACT_ID,
        token = TOKEN,
        owner = OWNER,
        createdDate = CREATE_DATE,
        status = status.toString(),
        statusDetails = statusDetails.toString(),
        mainProcurementCategory = MPC.toString(),
        language = LANGUAGE,
        jsonData = jsonData
    )
}