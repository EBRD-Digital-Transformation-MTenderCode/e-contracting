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
import com.procurement.contracting.application.repository.confirmation.ConfirmationResponseEntity
import com.procurement.contracting.application.repository.confirmation.ConfirmationResponseRepository
import com.procurement.contracting.domain.model.bid.BusinessFunctionType
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestId
import com.procurement.contracting.domain.model.confirmation.response.ConfirmationResponse
import com.procurement.contracting.domain.model.confirmation.response.ConfirmationResponseType
import com.procurement.contracting.domain.model.document.type.DocumentTypeBF
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
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DatabaseTestConfiguration::class])
class CassandraConfirmationResponseRepositoryIT {

    companion object {
        private val CPID = Cpid.orNull("ocds-b3wdp1-MD-1580458690892")!!
        private val OCID = Ocid.orNull("ocds-b3wdp1-MD-1580458690892-AC-1580123456789")!!
        private val CONTRACT_ID = "ocds-b3wdp1-MD-1580458690892-AC-1580123456711"
        private val CONFIRMATION_RESPONSE_ID = UUID.randomUUID().toString()
        private val CONFIRMATION_REQUEST_ID = ConfirmationRequestId.orNull(UUID.randomUUID().toString())!!
    }

    private val mapper = jacksonObjectMapper().apply { configuration() }
    private val transform = JacksonJsonTransform(mapper)

    @Autowired
    private lateinit var container: CassandraTestContainer

    private lateinit var session: Session
    private lateinit var confirmationResponseRepository: ConfirmationResponseRepository

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

        confirmationResponseRepository = CassandraConfirmationResponseRepository(session)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun `should successful save one record`() {
        val expectedStoredEntitiesAmount = 1
        val expectedEntity = generateConfirmationResponseEntity()
            .copy(
                requestId = CONFIRMATION_REQUEST_ID,
                jsonData = " some json data "
            )

        confirmationResponseRepository.save(expectedEntity)

        val storedEntities: List<ConfirmationResponseEntity> = confirmationResponseRepository.findBy(cpid = CPID, ocid = OCID).get()
        assertTrue(storedEntities.isNotEmpty())
        assertEquals(expectedStoredEntitiesAmount, storedEntities.size)

        val actualEntity = storedEntities.first()
        assertEquals(expectedEntity, actualEntity)
    }

    @Test
    fun `should successful parse stored record`() {
        val expectedConfirmationRequest = generateConfirmationResponse()
        val expectedEntity = ConfirmationResponseEntity.of(CPID, OCID, CONTRACT_ID, expectedConfirmationRequest, transform).get()

        confirmationResponseRepository.save(expectedEntity)

        val actualEntity: ConfirmationResponseEntity = confirmationResponseRepository.findBy(cpid = CPID, ocid = OCID).get().first()

        val confirmationResponseResult = transform.tryDeserialization(actualEntity.jsonData, ConfirmationResponse::class.java)
        assertTrue(confirmationResponseResult.isSuccess)

        val confirmationResponse = confirmationResponseResult.get()
        assertEquals(expectedConfirmationRequest, confirmationResponse)
    }

    @Test
    fun `should fails when saving the same record twice`() {
        val expectedEntity = generateConfirmationResponseEntity()
            .copy(
                requestId = CONFIRMATION_REQUEST_ID,
                jsonData = " some json data "
            )

        confirmationResponseRepository.save(expectedEntity)

        val wasApplied = confirmationResponseRepository.save(expectedEntity).get()
        assertFalse(wasApplied)
    }

    @Test
    fun `should fail if proplem with database`() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val expectedEntity = generateConfirmationResponseEntity()
            .copy(
                requestId = CONFIRMATION_REQUEST_ID,
                jsonData = " some json data "
            )

        val exception = assertThrows<SaveEntityException> {
            confirmationResponseRepository.save(expectedEntity).orThrow { it.exception }
        }
        assertTrue(exception.message!!.contains("Error writing"))
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
               CREATE TABLE IF NOT EXISTS ocds.contracting_confirmation_responses
                    (
                        cpid           TEXT,
                        ocid           TEXT,
                        contract_id    TEXT,
                        id             TEXT,
                        request_id     TEXT,
                        json_data      TEXT,
                        PRIMARY KEY (cpid, ocid, contract_id, id)
                    );
            """
        )
    }

    private fun generateConfirmationResponseEntity() = ConfirmationResponseEntity(
        cpid = CPID,
        ocid = OCID,
        contractId = CONTRACT_ID,
        id = CONFIRMATION_RESPONSE_ID,
        requestId = CONFIRMATION_REQUEST_ID,
        jsonData = ""
    )

    private fun generateConfirmationResponse() = ConfirmationResponse(
        id = UUID.randomUUID().toString(),
        type = ConfirmationResponseType.CODE,
        date = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
        requestId = CONFIRMATION_REQUEST_ID,
        value = "responseValue",
        relatedPerson = ConfirmationResponse.Person(
            id = "person.id",
            title = "person.title",
            name = "person.name",
            identifier = ConfirmationResponse.Person.Identifier(
                        id = "identifier.id",
                        scheme = "identifier.scheme",
                        uri = "identifier.uri"
                    ),
            businessFunctions = listOf(
                ConfirmationResponse.Person.BusinessFunction(
                    id = "businessFunction.id",
                    type = BusinessFunctionType.TECHNICAL_EVALUATOR,
                    jobTitle = "businessFunction.jobTitle",
                    period = ConfirmationResponse.Person.BusinessFunction.Period(
                        startDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                    ),
                    documents = listOf(
                        ConfirmationResponse.Person.BusinessFunction.Document(
                            id = "document.id",
                            documentType = DocumentTypeBF.REGULATORY_DOCUMENT,
                            title = "document.title",
                            description = "document.description"
                        )
                    )
                )
            )

        )
    )
}
