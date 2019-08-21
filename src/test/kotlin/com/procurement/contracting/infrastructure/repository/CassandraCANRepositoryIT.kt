package com.procurement.contracting.infrastructure.repository

import com.datastax.driver.core.BatchStatement
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
import com.procurement.contracting.application.repository.CANRepository
import com.procurement.contracting.application.repository.DataCancelCAN
import com.procurement.contracting.application.repository.DataRelatedCAN
import com.procurement.contracting.application.repository.DataStatusesCAN
import com.procurement.contracting.domain.entity.CANEntity
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.lot.LotId
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
class CassandraCANRepositoryIT {
    companion object {
        private const val CPID = "cpid-1"
        private val TOKEN = UUID.randomUUID()
        private const val OWNER = "owner-1"
        private val CREATE_DATE = LocalDateTime.now()
        private const val CONTRACT_ID = "ac-id-1"
        private val AWARD_ID: AwardId = UUID.randomUUID()
        private val CAN_LOT_ID: LotId = UUID.fromString("eb7a7343-c48c-481f-bcd2-7e4d14966e1d")
        private val RELATED_CAN_LOT_ID: LotId = UUID.fromString("db503a04-780a-49fb-839d-7836a49fa28c")

        private val CAN_ID: CANId = UUID.randomUUID()
        private val CAN_STATUS = CANStatus.PENDING
        private val CAN_STATUS_AFTER_CANCEL = CANStatus.CANCELLED
        private val CAN_STATUS_AFTER_UPDATE = CANStatus.UNSUCCESSFUL
        private val CAN_STATUS_DETAILS = CANStatusDetails.ACTIVE
        private val CAN_STATUS_DETAILS_AFTER_CANCEL = CANStatusDetails.EMPTY
        private val CAN_STATUS_DETAILS_AFTER_UPDATE = CANStatusDetails.TREASURY_REJECTION
        private const val JSON_DATA_CAN = """ {"can": "data"} """
        private const val JSON_DATA_CANCELLED_CAN = """ {"can": "canceled data"} """
        private const val JSON_DATA_UPDATED_CAN = """ {"can": "updated data"} """

        private val RELATED_CAN_ID: CANId = UUID.randomUUID()
        private val RELATED_CAN_STATUS = CANStatus.PENDING
        private val RELATED_CAN_STATUS_AFTER_CANCEL = CANStatus.CANCELLED
        private val RELATED_CAN_STATUS_DETAILS = CANStatusDetails.ACTIVE
        private val RELATED_CAN_STATUS_DETAILS_AFTER_CANCEL = CANStatusDetails.CONTRACT_PROJECT
        private const val JSON_DATA_RELATED_CAN = """ {"related": "data"} """
        private const val JSON_DATA_CANCELLED_RELATED_CAN = """ {"related": "canceled data"} """
    }

    @Autowired
    private lateinit var container: CassandraTestContainer

    private lateinit var session: Session
    private lateinit var canRepository: CANRepository

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

        canRepository = CassandraCANRepository(session)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun findByCPIDAndCANId() {
        insertCAN()

        val actualFundedCAN: CANEntity? = canRepository.findBy(cpid = CPID, canId = CAN_ID)

        assertNotNull(actualFundedCAN)
        assertEquals(expectedFundedCAN(), actualFundedCAN)
    }

    @Test
    fun canByCPIDAndCANIdNotFound() {
        val actualFundedCAN = canRepository.findBy(cpid = "UNKNOWN", canId = CAN_ID)
        assertNull(actualFundedCAN)
    }

    @Test
    fun findByCPID() {
        insertCAN()
        insertRelatedCAN()

        val actualFundedCANs: List<CANEntity> = canRepository.findBy(cpid = CPID)

        assertNotNull(actualFundedCANs)
        assertEquals(2, actualFundedCANs.size)

        val actualFundedCAN = actualFundedCANs.find { it.id == CAN_ID }
        assertNotNull(actualFundedCAN)
        assertEquals(expectedFundedCAN(), actualFundedCAN)

        val actualFundedRelatedCAN = actualFundedCANs.find { it.id == RELATED_CAN_ID }
        assertNotNull(actualFundedRelatedCAN)
        assertEquals(expectedFundedRelatedCAN(), actualFundedRelatedCAN)
    }

    @Test
    fun canByCPIDNotFound() {
        val actualFundedCANs = canRepository.findBy(cpid = "UNKNOWN")
        assertNotNull(actualFundedCANs)
        assertEquals(0, actualFundedCANs.size)
    }

    @Test
    fun errorRead() {
        insertCAN()

        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val exception = assertThrows<ReadEntityException> {
            canRepository.findBy(cpid = CPID, canId = CAN_ID)
        }
        assertEquals("Error read CAN(s) from the database.", exception.message)
    }

    @Test
    fun saveCancelledCANs() {
        insertCAN()
        insertRelatedCAN()

        canRepository.saveCancelledCANs(
            cpid = CPID,
            dataCancelledCAN = dataCancelledCAN(),
            dataRelatedCANs = dataRelatedCANs()
        )

        val actualCancelledCAN: CANEntity? = canRepository.findBy(cpid = CPID, canId = CAN_ID)
        assertNotNull(actualCancelledCAN)
        assertEquals(expectedCancelledCAN(), actualCancelledCAN)

        val actualRelatedCAN: CANEntity? = canRepository.findBy(cpid = CPID, canId = RELATED_CAN_ID)
        assertNotNull(actualRelatedCAN)
        assertEquals(expectedCancelledRelatedCAN(), actualRelatedCAN)
    }

    @Test
    fun errorSaveUnknownCANs() {
        insertCAN()

        val exception = assertThrows<SaveEntityException> {
            canRepository.saveCancelledCANs(
                cpid = CPID,
                dataCancelledCAN = dataCancelledCAN(),
                dataRelatedCANs = dataRelatedCANs()
            )
        }

        assertEquals(
            "An error occurred when writing a record(s) of the CAN(s) by cpid '$CPID' from the database.",
            exception.message
        )
    }

    @Test
    fun errorSaveCancelledCANs() {
        insertCAN()

        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BatchStatement>())

        val exception = assertThrows<SaveEntityException> {
            canRepository.saveCancelledCANs(
                cpid = CPID,
                dataCancelledCAN = dataCancelledCAN(),
                dataRelatedCANs = emptyList()
            )
        }
        assertEquals("Error writing cancelled CAN(s).", exception.message)
    }

    @Test
    fun saveUpdatedStatusesCANs() {
        insertCAN()

        val updatedCan = DataStatusesCAN(
            id = CAN_ID,
            status = CAN_STATUS_AFTER_UPDATE,
            statusDetails = CAN_STATUS_DETAILS_AFTER_UPDATE,
            jsonData = JSON_DATA_UPDATED_CAN
        )

        canRepository.updateStatusesCANs(cpid = CPID, cans = listOf(updatedCan))

        val actualCANEntity: CANEntity? = canRepository.findBy(cpid = CPID, canId = CAN_ID)

        val expectedCANEntity = expectedUpdatedCAN()

        assertNotNull(actualCANEntity)
        assertEquals(expectedCANEntity, actualCANEntity)
    }

    @Test
    fun errorSaveUpdatedStatusesUnknownCANs() {
        val updatedCan = DataStatusesCAN(
            id = CAN_ID,
            status = CAN_STATUS_AFTER_UPDATE,
            statusDetails = CAN_STATUS_DETAILS_AFTER_UPDATE,
            jsonData = JSON_DATA_UPDATED_CAN
        )

        val exception = assertThrows<SaveEntityException> {
            canRepository.updateStatusesCANs(cpid = CPID, cans = listOf(updatedCan))
        }

        assertEquals(
            "An error occurred when writing a record(s) of the CAN(s) by cpid '$CPID' from the database.",
            exception.message
        )
    }

    @Test
    fun errorSaveUpdatedStatusesCANs() {
        insertCAN()

        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BatchStatement>())

        val updatedCan = DataStatusesCAN(
            id = CAN_ID,
            status = CAN_STATUS_AFTER_UPDATE,
            statusDetails = CAN_STATUS_DETAILS_AFTER_UPDATE,
            jsonData = JSON_DATA_UPDATED_CAN
        )

        val exception = assertThrows<SaveEntityException> {
            canRepository.updateStatusesCANs(cpid = CPID, cans = listOf(updatedCan))
        }
        assertEquals("Error writing updated statuses CAN(s).", exception.message)
    }

    @Test
    fun saveNewCAN() {
        val newCAN = createCANEntity(
            id = CAN_ID,
            lotId = CAN_LOT_ID,
            contractId = CONTRACT_ID,
            status = CAN_STATUS,
            statusDetails = CAN_STATUS_DETAILS,
            jsonData = JSON_DATA_CAN
        )

        canRepository.saveNewCAN(cpid = CPID, entity = newCAN)

        val actualCancelledCAN: CANEntity? = canRepository.findBy(cpid = CPID, canId = CAN_ID)

        assertNotNull(actualCancelledCAN)
        assertEquals(newCAN, actualCancelledCAN)
    }

    @Test
    fun errorSaveDuplicateCAN() {
        val newCAN = createCANEntity(
            id = CAN_ID,
            lotId = CAN_LOT_ID,
            contractId = CONTRACT_ID,
            status = CAN_STATUS,
            statusDetails = CAN_STATUS_DETAILS,
            jsonData = JSON_DATA_CAN
        )

        canRepository.saveNewCAN(cpid = CPID, entity = newCAN)

        val exception = assertThrows<SaveEntityException> {
            canRepository.saveNewCAN(cpid = CPID, entity = newCAN)
        }

        assertEquals(
            "An error occurred when writing a record(s) of new CAN by cpid '$CPID' and lot id '${newCAN.lotId}' and award id '${newCAN.awardId}' to the database. Record is already.",
            exception.message
        )
    }

    @Test
    fun errorSaveNewCAN() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val newCAN = createCANEntity(
            id = CAN_ID,
            lotId = CAN_LOT_ID,
            contractId = CONTRACT_ID,
            status = CAN_STATUS,
            statusDetails = CAN_STATUS_DETAILS,
            jsonData = JSON_DATA_CAN
        )

        val exception = assertThrows<SaveEntityException> {
            canRepository.saveNewCAN(cpid = CPID, entity = newCAN)
        }
        assertEquals("Error writing new CAN.", exception.message)
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
                CREATE TABLE IF NOT EXISTS  ocds.contracting_can (
                    cp_id text,
                    can_id UUID,
                    token_entity UUID,
                    owner text,
                    created_date timestamp,
                    award_id text,
                    lot_id text,
                    ac_id text,
                    status text,
                    status_details text,
                    json_data text,
                    PRIMARY KEY(cp_id, can_id)
                );
            """
        )
    }

    private fun insertCAN(
        canId: CANId = CAN_ID,
        lotId: LotId = CAN_LOT_ID,
        status: CANStatus = CAN_STATUS,
        statusDetails: CANStatusDetails = CAN_STATUS_DETAILS,
        jsonData: String = JSON_DATA_CAN
    ) {
        val rec = QueryBuilder.insertInto("ocds", "contracting_can")
            .value("cp_id", CPID)
            .value("can_id", canId)
            .value("token_entity", TOKEN)
            .value("owner", OWNER)
            .value("created_date", CREATE_DATE.toCassandraTimestamp())
            .value("award_id", AWARD_ID.toString())
            .value("lot_id", lotId.toString())
            .value("ac_id", CONTRACT_ID)
            .value("status", status.toString())
            .value("status_details", statusDetails.toString())
            .value("json_data", jsonData)

        session.execute(rec)
    }

    private fun insertRelatedCAN() = insertCAN(
        canId = RELATED_CAN_ID,
        lotId = RELATED_CAN_LOT_ID,
        status = RELATED_CAN_STATUS,
        statusDetails = RELATED_CAN_STATUS_DETAILS,
        jsonData = JSON_DATA_RELATED_CAN
    )

    private fun dataCancelledCAN() = DataCancelCAN(
        id = CAN_ID,
        status = CAN_STATUS_AFTER_CANCEL,
        statusDetails = CAN_STATUS_DETAILS_AFTER_CANCEL,
        jsonData = JSON_DATA_CANCELLED_CAN
    )

    private fun dataRelatedCANs() = listOf(
        DataRelatedCAN(
            id = RELATED_CAN_ID,
            status = RELATED_CAN_STATUS_AFTER_CANCEL,
            statusDetails = RELATED_CAN_STATUS_DETAILS_AFTER_CANCEL,
            jsonData = JSON_DATA_CANCELLED_RELATED_CAN
        )
    )

    private fun expectedFundedCAN() = createCANEntity(
        id = CAN_ID,
        lotId = CAN_LOT_ID,
        contractId = CONTRACT_ID,
        status = CAN_STATUS,
        statusDetails = CAN_STATUS_DETAILS,
        jsonData = JSON_DATA_CAN
    )

    private fun expectedFundedRelatedCAN() = createCANEntity(
        id = RELATED_CAN_ID,
        lotId = RELATED_CAN_LOT_ID,
        contractId = CONTRACT_ID,
        status = RELATED_CAN_STATUS,
        statusDetails = RELATED_CAN_STATUS_DETAILS,
        jsonData = JSON_DATA_RELATED_CAN
    )

    private fun expectedCancelledCAN() = createCANEntity(
        id = CAN_ID,
        lotId = CAN_LOT_ID,
        contractId = null,
        status = CAN_STATUS_AFTER_CANCEL,
        statusDetails = CAN_STATUS_DETAILS_AFTER_CANCEL,
        jsonData = JSON_DATA_CANCELLED_CAN
    )

    private fun expectedCancelledRelatedCAN() = createCANEntity(
        id = RELATED_CAN_ID,
        lotId = RELATED_CAN_LOT_ID,
        contractId = null,
        status = RELATED_CAN_STATUS_AFTER_CANCEL,
        statusDetails = RELATED_CAN_STATUS_DETAILS_AFTER_CANCEL,
        jsonData = JSON_DATA_CANCELLED_RELATED_CAN
    )

    private fun expectedUpdatedCAN() = createCANEntity(
        id = CAN_ID,
        lotId = CAN_LOT_ID,
        contractId = CONTRACT_ID,
        status = CAN_STATUS_AFTER_UPDATE,
        statusDetails = CAN_STATUS_DETAILS_AFTER_UPDATE,
        jsonData = JSON_DATA_UPDATED_CAN
    )

    private fun createCANEntity(
        id: CANId,
        lotId: LotId,
        awardId: AwardId = AWARD_ID,
        contractId: String?,
        status: CANStatus,
        statusDetails: CANStatusDetails,
        jsonData: String
    ) = CANEntity(
        cpid = CPID,
        id = id,
        token = TOKEN,
        owner = OWNER,
        createdDate = CREATE_DATE,
        awardId = awardId,
        lotId = lotId,
        contractId = contractId,
        status = status,
        statusDetails = statusDetails,
        jsonData = jsonData
    )
}