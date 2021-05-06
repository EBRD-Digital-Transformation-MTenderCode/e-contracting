package com.procurement.contracting.infrastructure.repository

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.HostDistance
import com.datastax.driver.core.PlainTextAuthProvider
import com.datastax.driver.core.PoolingOptions
import com.datastax.driver.core.Session
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doThrow
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.contracting.application.exception.repository.SaveEntityException
import com.procurement.contracting.application.repository.confirmation.ConfirmationRequestEntity
import com.procurement.contracting.application.repository.confirmation.ConfirmationRequestRepository
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequest
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestId
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestReleaseTo
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestSource
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestType
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.get
import com.procurement.contracting.infrastructure.bind.configuration
import com.procurement.contracting.infrastructure.service.JacksonJsonTransform
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DatabaseTestConfiguration::class])
class CassandraConfirmationRequestRepositoryIT {

    companion object {
        private val CPID = Cpid.orNull("ocds-b3wdp1-MD-1580458690892")!!
        private val OCID = Ocid.orNull("ocds-b3wdp1-MD-1580458690892-AC-1580123456789")!!
        private val CONTRACT_ID = "ocds-b3wdp1-MD-1580458690892-AC-1580123456711"
        private val CONFIRMATION_REQUEST_ID = ConfirmationRequestId.orNull(UUID.randomUUID().toString())!!
    }

    private val mapper = jacksonObjectMapper().apply { configuration() }
    private val transform = JacksonJsonTransform(mapper)

    @Autowired
    private lateinit var container: CassandraTestContainer

    private lateinit var session: Session
    private lateinit var confirmationRequestRepository: ConfirmationRequestRepository

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

        confirmationRequestRepository = CassandraConfirmationRequestRepository(session)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun `should successful save one record`() {
        val expectedStoredEntitiesAmount = 1
        val expectedEntity = generateConfirmationRequestEntity()
            .copy(
                requests = setOf("1","2","4"),
                jsonData = " some json data "
            )

        confirmationRequestRepository.save(expectedEntity)

        val storedEntities: List<ConfirmationRequestEntity> = confirmationRequestRepository.findBy(cpid = CPID, ocid = OCID).get()
        assertTrue(storedEntities.isNotEmpty())
        assertEquals(expectedStoredEntitiesAmount, storedEntities.size)

        val actualEntity = storedEntities.first()
        assertEquals(expectedEntity, actualEntity)
    }

    @Test
    fun `should successful parse stored record`() {
        val expectedConfirmationRequest = generateConfirmationRequest()
        val expectedEntity = ConfirmationRequestEntity.of(CPID, OCID, CONTRACT_ID, expectedConfirmationRequest, transform).get()

        confirmationRequestRepository.save(expectedEntity)

        val actualEntity: ConfirmationRequestEntity = confirmationRequestRepository.findBy(cpid = CPID, ocid = OCID).get().first()

        val confirmationRequestResult = transform.tryDeserialization(actualEntity.jsonData, ConfirmationRequest::class.java)
        assertTrue(confirmationRequestResult.isSuccess)

        val confirmationRequest = confirmationRequestResult.get()
        assertEquals(expectedConfirmationRequest, confirmationRequest)
    }

    @Test
    fun `should fails when saving the same record twice`() {
        val expectedEntity = generateConfirmationRequestEntity()
            .copy(
                requests = setOf("1","2","4"),
                jsonData = " some json data "
            )

        confirmationRequestRepository.save(expectedEntity)

        val wasApplied = confirmationRequestRepository.save(expectedEntity).get()
        assertFalse(wasApplied)
    }

    @Test
    fun `should fail if proplem with database`() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val expectedEntity = generateConfirmationRequestEntity()
            .copy(
                requests = setOf("1","2","4"),
                jsonData = " some json data "
            )

        val exception = assertThrows<SaveEntityException> {
            confirmationRequestRepository.save(expectedEntity).orThrow { it.exception }
        }
        assertTrue(exception.message!!.contains("Error writing"))
    }

    @Test
    fun `should successful find by contract id`() {
        val expectedStoredEntitiesAmount = 1
        val expectedConfirmationRequest = generateConfirmationRequest()
        val expectedEntity = ConfirmationRequestEntity.of(CPID, OCID, CONTRACT_ID, expectedConfirmationRequest, transform).get()

        confirmationRequestRepository.save(expectedEntity)

        val storedEntities: List<ConfirmationRequestEntity> = confirmationRequestRepository.findBy(cpid = CPID, ocid = OCID, CONTRACT_ID).get()

        assertTrue(storedEntities.isNotEmpty())
        assertEquals(expectedStoredEntitiesAmount, storedEntities.size)

        val actualEntity = storedEntities.first()
        assertEquals(expectedEntity, actualEntity)
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
                 CREATE TABLE IF NOT EXISTS ocds.contracting_confirmation_requests
                    (
                        cpid           TEXT,
                        ocid           TEXT,
                        contract_id    TEXT,
                        id             TEXT,
                        requests       set<TEXT>,
                        json_data      TEXT,
                        PRIMARY KEY (cpid, ocid, contract_id, id)
                    );
            """
        )
    }

    private fun generateConfirmationRequestEntity() = ConfirmationRequestEntity(
        cpid = CPID,
        ocid = OCID,
        contractId = CONTRACT_ID,
        id = CONFIRMATION_REQUEST_ID,
        requests = emptySet(),
        jsonData = ""
    )

    private fun generateConfirmationRequest() = ConfirmationRequest(
        id = ConfirmationRequestId.orNull(UUID.randomUUID().toString())!!,
        type = ConfirmationRequestType.DIGITAL_SIGNATURE,
        relatesTo = ConfirmationRequestReleaseTo.CONTRACT,
        relatedItem = UUID.randomUUID().toString(),
        source = ConfirmationRequestSource.INVITED_CANDIDATE,
        requests = listOf(
            ConfirmationRequest.Request(
                id = UUID.randomUUID().toString(),
                owner = UUID.randomUUID().toString(),
                token = Token.generate(),
                relatedOrganization = ConfirmationRequest.Request.Organization(
                    id = UUID.randomUUID().toString(),
                    name = "SAMPLE BUYER INC"
                )
            )
        )
    )
}
