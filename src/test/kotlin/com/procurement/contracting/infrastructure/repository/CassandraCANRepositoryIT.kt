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
import com.procurement.contracting.application.exception.repository.SaveEntityException
import com.procurement.contracting.application.repository.can.CANRepository
import com.procurement.contracting.application.repository.can.model.DataCancelCAN
import com.procurement.contracting.application.repository.can.model.DataRelatedCAN
import com.procurement.contracting.application.repository.can.model.RelatedContract
import com.procurement.contracting.assertFailure
import com.procurement.contracting.domain.entity.CANEntity
import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.contract.id.ContractId
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.domain.model.process.Cpid
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
import java.util.*

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DatabaseTestConfiguration::class])
class CassandraCANRepositoryIT {
    companion object {
        private val CPID = Cpid.orNull("ocds-b3wdp1-MD-1580458690892")!!
        private val TOKEN = Token.generate()
        private val OWNER = Owner.orNull("d0da4c24-1a2a-4b39-a1fd-034cb887c93b")!!
        private val CREATE_DATE = LocalDateTime.now()
        private val CONTRACT_ID = ContractId.generate(CPID)
        private val AWARD_ID: AwardId = AwardId.generate()
        private val CAN_LOT_ID: LotId = LotId.orNull("eb7a7343-c48c-481f-bcd2-7e4d14966e1d")!!
        private val RELATED_CAN_LOT_ID: LotId = LotId.orNull("db503a04-780a-49fb-839d-7836a49fa28c")!!

        private val CAN_ID: CANId = CANId.orNull(UUID.randomUUID().toString())!!
        private val CAN_STATUS = CANStatus.PENDING
        private val CAN_STATUS_AFTER_CANCEL = CANStatus.CANCELLED
        private val CAN_STATUS_AFTER_UPDATE = CANStatus.UNSUCCESSFUL
        private val CAN_STATUS_DETAILS = CANStatusDetails.ACTIVE
        private val CAN_STATUS_DETAILS_AFTER_CANCEL = CANStatusDetails.EMPTY
        private val CAN_STATUS_DETAILS_AFTER_UPDATE = CANStatusDetails.TREASURY_REJECTION
        private const val JSON_DATA_CAN = """ {"can": "data"} """
        private const val JSON_DATA_CANCELLED_CAN = """ {"can": "canceled data"} """
        private const val JSON_DATA_UPDATED_CAN = """ {"can": "updated data"} """

        private val RELATED_CAN_ID: CANId = CANId.orNull(UUID.randomUUID().toString())!!
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
        insertCANWithContract()

        val actualFundedCAN: CANEntity? = canRepository.findBy(cpid = CPID, canId = CAN_ID).get()

        assertNotNull(actualFundedCAN)
        assertEquals(expectedFundedCAN(), actualFundedCAN)
    }

    @Test
    fun canByCPIDAndCANIdNotFound() {
        val actualFundedCAN =
            canRepository.findBy(cpid = Cpid.orNull("ocds-b3wdp1-MD-0000000000000")!!, canId = CAN_ID).get()
        assertNull(actualFundedCAN)
    }

    @Test
    fun findByCPID() {
        insertCANWithContract()
        insertRelatedCAN()

        val actualFundedCANs: List<CANEntity> = canRepository.findBy(cpid = CPID).get()

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
        val actualFundedCANs =
            canRepository.findBy(cpid = Cpid.orNull("ocds-b3wdp1-MD-0000000000000")!!).get()
        assertNotNull(actualFundedCANs)
        assertEquals(0, actualFundedCANs.size)
    }

    @Test
    fun errorRead() {
        insertCANWithContract()

        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        canRepository.findBy(cpid = CPID, canId = CAN_ID).assertFailure()
    }

    @Test
    fun saveCancelledCANs() {
        insertCANWithContract()
        insertRelatedCAN()

        canRepository.saveCancelledCANs(
            cpid = CPID,
            dataCancelledCAN = dataCancelledCAN(),
            dataRelatedCANs = dataRelatedCANs()
        )

        val actualCancelledCAN: CANEntity? = canRepository.findBy(cpid = CPID, canId = CAN_ID).get()
        assertNotNull(actualCancelledCAN)
        assertEquals(expectedCancelledCAN(), actualCancelledCAN)

        val actualRelatedCAN: CANEntity? = canRepository.findBy(cpid = CPID, canId = RELATED_CAN_ID).get()
        assertNotNull(actualRelatedCAN)
        assertEquals(expectedCancelledRelatedCAN(), actualRelatedCAN)
    }

    @Test
    fun errorSaveUnknownCANs() {
        insertCANWithContract()

        val wasApplied = canRepository
            .saveCancelledCANs(
                cpid = CPID,
                dataCancelledCAN = dataCancelledCAN(),
                dataRelatedCANs = dataRelatedCANs()
            )
            .get()

        assertFalse(wasApplied)
    }

    @Test
    fun errorSaveCancelledCANs() {
        insertCANWithContract()

        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BatchStatement>())

        val exception = assertThrows<SaveEntityException> {
            canRepository.saveCancelledCANs(
                cpid = CPID,
                dataCancelledCAN = dataCancelledCAN(),
                dataRelatedCANs = emptyList()
            ).orThrow { it.exception }
        }
        assertEquals("Error writing cancelled CAN(s).", exception.message)
    }

    @Test
    fun saveCANsWithRelatedContract() {
        insertCANWithoutContract()

        val relatedContract = RelatedContract(
            id = CAN_ID,
            contractId = CONTRACT_ID,
            status = CAN_STATUS_AFTER_UPDATE,
            statusDetails = CAN_STATUS_DETAILS_AFTER_UPDATE,
            jsonData = JSON_DATA_UPDATED_CAN
        )

        canRepository.relateContract(cpid = CPID, cans = listOf(relatedContract))

        val actualCANEntity: CANEntity? = canRepository.findBy(cpid = CPID, canId = CAN_ID).get()
        assertNotNull(actualCANEntity)
        val expectedCANEntity = expectedUpdatedCAN()
        assertEquals(expectedCANEntity, actualCANEntity)
    }

    @Test
    fun errorSaveUnknownCANsWithRelatedContract() {
        val relatedContract = RelatedContract(
            id = CAN_ID,
            contractId = CONTRACT_ID,
            status = CAN_STATUS_AFTER_UPDATE,
            statusDetails = CAN_STATUS_DETAILS_AFTER_UPDATE,
            jsonData = JSON_DATA_UPDATED_CAN
        )

        val wasApplied = canRepository.relateContract(cpid = CPID, cans = listOf(relatedContract)).get()
        assertFalse(wasApplied)
    }

    @Test
    fun errorSaveCANsWithRelatedContract() {
        insertCANWithoutContract()

        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BatchStatement>())

        val relatedContract = RelatedContract(
            id = CAN_ID,
            contractId = CONTRACT_ID,
            status = CAN_STATUS_AFTER_UPDATE,
            statusDetails = CAN_STATUS_DETAILS_AFTER_UPDATE,
            jsonData = JSON_DATA_UPDATED_CAN
        )

        val exception = assertThrows<SaveEntityException> {
            canRepository.relateContract(cpid = CPID, cans = listOf(relatedContract)).orThrow { it.exception }
        }
        assertEquals("Error writing updated CAN(s) with related Contract.", exception.message)
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

        val actualCancelledCAN: CANEntity? = canRepository.findBy(cpid = CPID, canId = CAN_ID).get()

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

        val wasApplied = canRepository.saveNewCAN(cpid = CPID, entity = newCAN).get()
        assertFalse(wasApplied)
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
            canRepository.saveNewCAN(cpid = CPID, entity = newCAN).orThrow { it.exception }
        }
        assertEquals("Error writing new CAN.", exception.message)
    }

    @Test
    fun updateOne() {
        val newCAN = createCANEntity(
            id = CAN_ID,
            lotId = CAN_LOT_ID,
            contractId = CONTRACT_ID,
            status = CAN_STATUS,
            statusDetails = CAN_STATUS_DETAILS,
            jsonData = JSON_DATA_CAN
        )

        canRepository.saveNewCAN(cpid = CPID, entity = newCAN)

        val updatedCAN = newCAN.copy(
            status = CAN_STATUS_AFTER_UPDATE,
            statusDetails = CAN_STATUS_DETAILS_AFTER_UPDATE,
            jsonData = JSON_DATA_UPDATED_CAN
        )
        val wasApplied = canRepository.update(cpid = CPID, entity = updatedCAN).get()
        assertTrue(wasApplied)

        val actualCAN: CANEntity? = canRepository.findBy(cpid = CPID, canId = CAN_ID).get()

        assertNotNull(actualCAN)
        assertEquals(updatedCAN, actualCAN)
    }

    @Test
    fun updateMulti() {
        val newCAN = createCANEntity(
            id = CAN_ID,
            lotId = CAN_LOT_ID,
            contractId = CONTRACT_ID,
            status = CAN_STATUS,
            statusDetails = CAN_STATUS_DETAILS,
            jsonData = JSON_DATA_CAN
        )

        canRepository.saveNewCAN(cpid = CPID, entity = newCAN)

        val updatedCAN = newCAN.copy(
            status = CAN_STATUS_AFTER_UPDATE,
            statusDetails = CAN_STATUS_DETAILS_AFTER_UPDATE,
            jsonData = JSON_DATA_UPDATED_CAN
        )
        val wasApplied = canRepository.update(cpid = CPID, entities = listOf(updatedCAN)).get()
        assertTrue(wasApplied)

        val actualCAN: CANEntity? = canRepository.findBy(cpid = CPID, canId = CAN_ID).get()

        assertNotNull(actualCAN)
        assertEquals(updatedCAN, actualCAN)
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

    private fun insertCANWithContract(
        canId: CANId = CAN_ID,
        lotId: LotId = CAN_LOT_ID,
        status: CANStatus = CAN_STATUS,
        statusDetails: CANStatusDetails = CAN_STATUS_DETAILS,
        jsonData: String = JSON_DATA_CAN
    ) {
        val rec = QueryBuilder.insertInto("ocds", "contracting_can")
            .value("cp_id", CPID.underlying)
            .value("can_id", canId.underlying)
            .value("token_entity", TOKEN.underlying)
            .value("owner", OWNER.underlying)
            .value("created_date", CREATE_DATE.toCassandraTimestamp())
            .value("award_id", AWARD_ID.toString())
            .value("lot_id", lotId.underlying)
            .value("ac_id", CONTRACT_ID.underlying)
            .value("status", status.key)
            .value("status_details", statusDetails.key)
            .value("json_data", jsonData)

        session.execute(rec)
    }

    private fun insertCANWithoutContract(
        canId: CANId = CAN_ID,
        lotId: LotId = CAN_LOT_ID,
        status: CANStatus = CAN_STATUS,
        statusDetails: CANStatusDetails = CAN_STATUS_DETAILS,
        jsonData: String = JSON_DATA_CAN
    ) {
        val rec = QueryBuilder.insertInto("ocds", "contracting_can")
            .value("cp_id", CPID.underlying)
            .value("can_id", canId.underlying)
            .value("token_entity", TOKEN.underlying)
            .value("owner", OWNER.underlying)
            .value("created_date", CREATE_DATE.toCassandraTimestamp())
            .value("award_id", AWARD_ID.toString())
            .value("lot_id", lotId.underlying)
            .value("status", status.key)
            .value("status_details", statusDetails.key)
            .value("json_data", jsonData)

        session.execute(rec)
    }

    private fun insertRelatedCAN() = insertCANWithContract(
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
        contractId: ContractId?,
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
