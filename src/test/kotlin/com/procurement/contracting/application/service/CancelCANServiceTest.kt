package com.procurement.contracting.application.service

import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.contracting.application.repository.ACRepository
import com.procurement.contracting.application.repository.CANRepository
import com.procurement.contracting.domain.entity.ACEntity
import com.procurement.contracting.domain.entity.CANEntity
import com.procurement.contracting.domain.model.CAN
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.json.loadJson
import com.procurement.contracting.model.dto.ContractProcess
import com.procurement.contracting.model.dto.ocds.ContractStatus
import com.procurement.contracting.model.dto.ocds.ContractStatusDetails
import com.procurement.contracting.model.dto.ocds.DocumentTypeAmendment
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.*
import java.util.stream.Stream
import kotlin.streams.asStream

class CancelCANServiceTest {
    companion object {
        private const val CPID = "cpid-1"
        private val CAN_TOKEN: UUID = UUID.fromString("2909bc16-82c7-4281-8f35-3f0bb13476b8")
        private const val OWNER = "owner-1"
        private val CAN_ID: UUID = UUID.fromString("0dc181db-f5ae-4039-97c7-defcceef89a4")
        private const val LOT_ID: String = "lot-id-0"
        private const val CONTRACT_ID: String = "contract-id-1"

        private val cancellationCAN =
            toObject(CAN::class.java, loadJson("json/application/service/cancel/cancellation-can.json"))
        private val firstOtherCAN =
            toObject(CAN::class.java, loadJson("json/application/service/cancel/first-related-can.json"))
        private val secondOtherCAN =
            toObject(CAN::class.java, loadJson("json/application/service/cancel/second-related-can.json"))
        private val contractProcess =
            toObject(ContractProcess::class.java, loadJson("json/application/service/cancel/contract-process.json"))
    }

    private lateinit var canRepository: CANRepository
    private lateinit var acRepository: ACRepository

    private lateinit var service: CancelCANService

    @BeforeEach
    fun init() {
        canRepository = mock()
        acRepository = mock()

        service = CancelCANServiceImpl(canRepository = canRepository, acRepository = acRepository)
    }

    @ParameterizedTest(name = "status: ''{0}'' statusDetails: ''{1}''")
    @ArgumentsSource(SuccessCANStatusAndStatusDetails::class)
    fun cancelCANWithoutContract(canStatus: ContractStatus, canStatusDetails: ContractStatusDetails) {
        val cancellationCANEntity = canEntity(
            can = cancellationCAN.copy(
                status = canStatus,
                statusDetails = canStatusDetails
            ),
            contractID = null
        )

        whenever(canRepository.findBy(eq(CPID), eq(CAN_ID)))
            .thenReturn(cancellationCANEntity)
        whenever(acRepository.findBy(eq(CPID), eq(CONTRACT_ID)))
            .thenReturn(null)
        whenever(canRepository.findBy(eq(CPID)))
            .thenReturn(listOf(cancellationCANEntity))

        val data = data()
        val response = service.cancel(context = context(), data = data)

        assertEquals(1, response.cans.size)
        val canceledCAN = response.cans[0]
        assertEquals(cancellationCANEntity.id, canceledCAN.id)
        assertEquals(ContractStatus.CANCELLED, canceledCAN.status)
        assertEquals(ContractStatusDetails.EMPTY, canceledCAN.statusDetails)

        assertNotNull(canceledCAN.amendment)
        val amendment = canceledCAN.amendment!!
        assertEquals(data.amendment.rationale, amendment.rationale)
        assertEquals(data.amendment.description, amendment.description)
        assertEquals(data.amendment.documents!!.size, amendment.documents!!.size)

        val documents = amendment.documents!!
        assertEquals(data.amendment.documents!![0].id, documents[0].id)
        assertEquals(data.amendment.documents!![0].documentType, documents[0].documentType)
        assertEquals(data.amendment.documents!![0].title, documents[0].title)
        assertEquals(data.amendment.documents!![0].description, documents[0].description)

        assertEquals(LOT_ID, response.lotId)
        assertEquals(false, response.isCancelledAC)

        assertNull(response.contract)
    }

    @ParameterizedTest(name = "status: ''{0}'' statusDetails: ''{1}''")
    @ArgumentsSource(SuccessACStatusAndStatusDetails::class)
    fun cancelCANWithContractWithoutRelatedCANs(
        contractStatus: ContractStatus,
        contractStatusDetails: ContractStatusDetails
    ) {
        val cancellationCANEntity = canEntity(
            can = cancellationCAN.copy(
                status = CANStatus.success.first(),
                statusDetails = CANStatusDetails.success.first()
            )
        )
        whenever(canRepository.findBy(eq(CPID), eq(CAN_ID)))
            .thenReturn(cancellationCANEntity)

        val acEntity = acEntity(
            contractProcess = contractProcess.copy(
                contract = contractProcess.contract.copy(
                    status = contractStatus,
                    statusDetails = contractStatusDetails
                )
            )
        )
        whenever(acRepository.findBy(eq(CPID), eq(CONTRACT_ID)))
            .thenReturn(acEntity)

        whenever(canRepository.findBy(eq(CPID)))
            .thenReturn(listOf(cancellationCANEntity))

        val data = data()
        val response = service.cancel(context = context(), data = data)

        assertEquals(1, response.cans.size)
        val canceledCAN = response.cans[0]
        assertEquals(cancellationCANEntity.id, canceledCAN.id)
        assertEquals(ContractStatus.CANCELLED, canceledCAN.status)
        assertEquals(ContractStatusDetails.EMPTY, canceledCAN.statusDetails)

        assertNotNull(canceledCAN.amendment)
        val amendment = canceledCAN.amendment!!
        assertEquals(data.amendment.rationale, amendment.rationale)
        assertEquals(data.amendment.description, amendment.description)
        assertEquals(data.amendment.documents!!.size, amendment.documents!!.size)

        val documents = amendment.documents!!
        assertEquals(data.amendment.documents!![0].id, documents[0].id)
        assertEquals(data.amendment.documents!![0].documentType, documents[0].documentType)
        assertEquals(data.amendment.documents!![0].title, documents[0].title)
        assertEquals(data.amendment.documents!![0].description, documents[0].description)

        assertEquals(LOT_ID, response.lotId)
        assertEquals(true, response.isCancelledAC)

        assertNotNull(response.contract)
        val contract = response.contract!!
        assertEquals(CONTRACT_ID, contract.id)
        assertEquals(ContractStatus.CANCELLED, contract.status)
        assertEquals(ContractStatusDetails.EMPTY, contract.statusDetails)
    }

    @Test
    fun cancelCANWithContractWithRelatedCAN() {
        val cancellationCANEntity = canEntity(cancellationCAN)
        whenever(canRepository.findBy(eq(CPID), eq(CAN_ID)))
            .thenReturn(cancellationCANEntity)

        val acEntity = acEntity(contractProcess)
        whenever(acRepository.findBy(eq(CPID), eq(CONTRACT_ID)))
            .thenReturn(acEntity)

        val firstCANEntity = canEntity(can = firstOtherCAN)
        val secondCANEntity = canEntity(can = secondOtherCAN, contractID = "UNKNOWN")
        whenever(canRepository.findBy(eq(CPID)))
            .thenReturn(listOf(cancellationCANEntity, firstCANEntity, secondCANEntity))

        val data = data()
        val response = service.cancel(context = context(), data = data)

        assertEquals(LOT_ID, response.lotId)
        assertEquals(true, response.isCancelledAC)

        assertNotNull(response.contract)
        val contract = response.contract!!
        assertEquals(CONTRACT_ID, contract.id)
        assertEquals(ContractStatus.CANCELLED, contract.status)
        assertEquals(ContractStatusDetails.EMPTY, contract.statusDetails)

        assertEquals(2, response.cans.size)

        val canceledCAN = response.cans[0]
        assertEquals(cancellationCAN.id, canceledCAN.id)
        assertEquals(ContractStatus.CANCELLED, canceledCAN.status)
        assertEquals(ContractStatusDetails.EMPTY, canceledCAN.statusDetails)

        assertNotNull(canceledCAN.amendment)
        val amendment = canceledCAN.amendment!!
        assertEquals(data.amendment.rationale, amendment.rationale)
        assertEquals(data.amendment.description, amendment.description)
        assertEquals(data.amendment.documents!!.size, amendment.documents!!.size)

        val documents = amendment.documents!!
        assertEquals(data.amendment.documents!![0].id, documents[0].id)
        assertEquals(data.amendment.documents!![0].documentType, documents[0].documentType)
        assertEquals(data.amendment.documents!![0].title, documents[0].title)
        assertEquals(data.amendment.documents!![0].description, documents[0].description)

        val firstRelatedCAN = response.cans[1]
        assertEquals(firstOtherCAN.id, firstRelatedCAN.id)
        assertEquals(ContractStatus.PENDING, firstRelatedCAN.status)
        assertEquals(ContractStatusDetails.CONTRACT_PROJECT, firstRelatedCAN.statusDetails)

        assertNull(firstRelatedCAN.amendment)
    }

    @Test
    fun canNotFound() {
        whenever(canRepository.findBy(eq(CPID), eq(CAN_ID)))
            .thenReturn(null)

        val exception = assertThrows<ErrorException> {
            service.cancel(context = context(), data = data())
        }

        assertEquals(ErrorType.CAN_NOT_FOUND, exception.error)
    }

    @Test
    fun invalidOwner() {
        val canEntity = canEntity(can = cancellationCAN, owner = "UNKNOWN")
        whenever(canRepository.findBy(eq(CPID), eq(CAN_ID)))
            .thenReturn(canEntity)

        val exception = assertThrows<ErrorException> {
            service.cancel(context = context(), data = data())
        }

        assertEquals(ErrorType.INVALID_OWNER, exception.error)
    }

    @Test
    fun invalidToken() {
        val canEntity = canEntity(can = cancellationCAN, token = UUID.randomUUID())
        whenever(canRepository.findBy(eq(CPID), eq(CAN_ID)))
            .thenReturn(canEntity)

        val exception = assertThrows<ErrorException> {
            service.cancel(context = context(), data = data())
        }

        assertEquals(ErrorType.INVALID_TOKEN, exception.error)
    }

    @Test
    fun contractNotFound() {
        val canEntity = canEntity(cancellationCAN)
        whenever(canRepository.findBy(eq(CPID), eq(CAN_ID)))
            .thenReturn(canEntity)

        val exception = assertThrows<ErrorException> {
            service.cancel(context = context(), data = data())
        }

        assertEquals(ErrorType.CONTRACT_NOT_FOUND, exception.error)
    }

    @ParameterizedTest(name = "status: ''{0}''")
    @ArgumentsSource(FailCANStatus::class)
    fun invalidCANStatus(canStatus: ContractStatus) {
        val cancellationCANEntity = canEntity(
            can = cancellationCAN.copy(
                status = canStatus,
                statusDetails = CANStatusDetails.success.first()
            ),
            contractID = null
        )
        whenever(canRepository.findBy(eq(CPID), eq(CAN_ID)))
            .thenReturn(cancellationCANEntity)

        whenever(acRepository.findBy(eq(CPID), eq(CONTRACT_ID)))
            .thenReturn(null)

        whenever(canRepository.findBy(eq(CPID)))
            .thenReturn(listOf(cancellationCANEntity))

        val exception = assertThrows<ErrorException> {
            service.cancel(context = context(), data = data())
        }

        assertEquals(ErrorType.INVALID_CAN_STATUS, exception.error)
    }

    @ParameterizedTest(name = "status details: ''{0}''")
    @ArgumentsSource(FailCANStatusDetails::class)
    fun invalidCANStatusDetails(canStatusDetails: ContractStatusDetails) {
        val cancellationCANEntity = canEntity(
            can = cancellationCAN.copy(
                status = CANStatus.success.first(),
                statusDetails = canStatusDetails
            ),
            contractID = null
        )
        whenever(canRepository.findBy(eq(CPID), eq(CAN_ID)))
            .thenReturn(cancellationCANEntity)

        whenever(acRepository.findBy(eq(CPID), eq(CONTRACT_ID)))
            .thenReturn(null)

        whenever(canRepository.findBy(eq(CPID)))
            .thenReturn(listOf(cancellationCANEntity))

        val exception = assertThrows<ErrorException> {
            service.cancel(context = context(), data = data())
        }

        assertEquals(ErrorType.INVALID_CAN_STATUS_DETAILS, exception.error)
    }

    @ParameterizedTest(name = "status: ''{0}''")
    @ArgumentsSource(FailACStatus::class)
    fun invalidContractStatus(contractStatus: ContractStatus) {
        val cancellationCANEntity = canEntity(
            can = cancellationCAN.copy(
                status = CANStatus.success.first(),
                statusDetails = CANStatusDetails.success.first()
            )
        )
        whenever(canRepository.findBy(eq(CPID), eq(CAN_ID)))
            .thenReturn(cancellationCANEntity)

        val acEntity = acEntity(
            contractProcess.copy(
                contract = contractProcess.contract.copy(
                    status = contractStatus,
                    statusDetails = ACStatusDetails.success.first()
                )
            )
        )
        whenever(acRepository.findBy(eq(CPID), eq(CONTRACT_ID)))
            .thenReturn(acEntity)

        whenever(canRepository.findBy(eq(CPID)))
            .thenReturn(listOf(cancellationCANEntity))

        val exception = assertThrows<ErrorException> {
            service.cancel(context = context(), data = data())
        }

        assertEquals(ErrorType.CONTRACT_STATUS, exception.error)
    }

    @ParameterizedTest(name = "status details: ''{0}''")
    @ArgumentsSource(FailACStatusDetails::class)
    fun invalidContractStatusDetails(contractStatusDetails: ContractStatusDetails) {
        val cancellationCANEntity = canEntity(
            can = cancellationCAN.copy(
                status = CANStatus.success.first(),
                statusDetails = CANStatusDetails.success.first()
            )
        )
        whenever(canRepository.findBy(eq(CPID), eq(CAN_ID)))
            .thenReturn(cancellationCANEntity)

        val acEntity = acEntity(
            contractProcess.copy(
                contract = contractProcess.contract.copy(
                    status = ACStatus.success.first(),
                    statusDetails = contractStatusDetails
                )
            )
        )
        whenever(acRepository.findBy(eq(CPID), eq(CONTRACT_ID)))
            .thenReturn(acEntity)

        whenever(canRepository.findBy(eq(CPID)))
            .thenReturn(listOf(cancellationCANEntity))

        val exception = assertThrows<ErrorException> {
            service.cancel(context = context(), data = data())
        }

        assertEquals(ErrorType.CONTRACT_STATUS_DETAILS, exception.error)
    }

    private fun context(
        cpid: String = CPID,
        token: UUID = CAN_TOKEN,
        owner: String = OWNER,
        canId: UUID = CAN_ID
    ): CancelCANContext {
        return CancelCANContext(
            cpid = cpid,
            token = token,
            owner = owner,
            canId = canId
        )
    }

    private fun data(): CancelCANData {
        return CancelCANData(
            amendment = CancelCANData.Amendment(
                rationale = "amendment.rationale",
                description = "amendment.description",
                documents = listOf(
                    CancelCANData.Amendment.Document(
                        id = "amendment.documents[0].id",
                        documentType = DocumentTypeAmendment.CONTRACT_NOTICE,
                        title = "amendment.documents[0].title",
                        description = "amendment.documents[0].description"
                    )
                )
            )
        )
    }

    private fun canEntity(can: CAN, owner: String = OWNER, token: UUID? = null, contractID: String? = CONTRACT_ID) =
        CANEntity(
            cpid = CPID,
            id = can.id,
            token = token ?: can.token,
            owner = owner,
            createdDate = can.date,
            awardId = can.awardId,
            lotId = can.lotId,
            contractId = contractID,
            status = can.status.toString(),
            statusDetails = can.statusDetails.toString(),
            jsonData = toJson(can)
        )

    private fun acEntity(contractProcess: ContractProcess) = ACEntity(
        cpid = CPID,
        id = contractProcess.contract.id,
        token = UUID.fromString(contractProcess.contract.token),
        owner = OWNER,
        createdDate = contractProcess.contract.date!!,
        status = contractProcess.contract.status.toString(),
        statusDetails = contractProcess.contract.statusDetails.toString(),
        mainProcurementCategory = "",
        language = "RO",
        jsonData = toJson(contractProcess)
    )

    internal class SuccessCANStatusAndStatusDetails : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> = mutableListOf<Arguments>()
            .apply {
                for (status in CANStatus.success) {
                    for (statusDetails in CANStatusDetails.success) {
                        add(Arguments.of(status, statusDetails))
                    }
                }
            }.stream()
    }

    internal class SuccessACStatusAndStatusDetails : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> = mutableListOf<Arguments>()
            .apply {
                for (status in ACStatus.success) {
                    for (statusDetails in ACStatusDetails.success) {
                        add(Arguments.of(status, statusDetails))
                    }
                }
            }
            .stream()
    }

    internal class FailCANStatus : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> = CANStatus.fail
            .asSequence()
            .map {
                Arguments.of(it)
            }
            .asStream()
    }

    internal class FailCANStatusDetails : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> =
            CANStatusDetails.fail
                .asSequence()
                .map {
                    Arguments.of(it)
                }
                .asStream()
    }

    internal class FailACStatus : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> = ACStatus.fail
            .asSequence()
            .map {
                Arguments.of(it)
            }
            .asStream()
    }

    internal class FailACStatusDetails : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> = ACStatusDetails.fail
            .asSequence()
            .map {
                Arguments.of(it)
            }
            .asStream()
    }
}

abstract class Either<T : Enum<T>>(target: Class<T>) {
    protected enum class Type { SUCCESS, FAIL }

    val success: List<T>
    val fail: List<T>

    init {
        val grouped = target.enumConstants.groupBy { apply(it) }
        success = grouped.getValue(Type.SUCCESS)
        fail = grouped.getValue(Type.FAIL)
    }

    protected abstract fun apply(item: T): Type
}

object CANStatus : Either<ContractStatus>(ContractStatus::class.java) {
    override fun apply(item: ContractStatus): Type = when (item) {
        ContractStatus.PENDING -> Type.SUCCESS

        ContractStatus.ACTIVE,
        ContractStatus.CANCELLED,
        ContractStatus.COMPLETE,
        ContractStatus.TERMINATED,
        ContractStatus.UNSUCCESSFUL -> Type.FAIL
    }
}

object CANStatusDetails : Either<ContractStatusDetails>(ContractStatusDetails::class.java) {
    override fun apply(item: ContractStatusDetails): Type = when (item) {
        ContractStatusDetails.CONTRACT_PROJECT,
        ContractStatusDetails.ACTIVE,
        ContractStatusDetails.UNSUCCESSFUL -> Type.SUCCESS

        ContractStatusDetails.CONTRACT_PREPARATION,
        ContractStatusDetails.APPROVED,
        ContractStatusDetails.SIGNED,
        ContractStatusDetails.VERIFICATION,
        ContractStatusDetails.VERIFIED,
        ContractStatusDetails.CANCELLED,
        ContractStatusDetails.COMPLETE,
        ContractStatusDetails.ISSUED,
        ContractStatusDetails.APPROVEMENT,
        ContractStatusDetails.EXECUTION,
        ContractStatusDetails.EMPTY -> Type.FAIL
    }
}

object ACStatus : Either<ContractStatus>(ContractStatus::class.java) {
    override fun apply(item: ContractStatus): Type = when (item) {
        ContractStatus.PENDING -> Type.SUCCESS

        ContractStatus.ACTIVE,
        ContractStatus.CANCELLED,
        ContractStatus.COMPLETE,
        ContractStatus.TERMINATED,
        ContractStatus.UNSUCCESSFUL -> Type.FAIL
    }
}

object ACStatusDetails : Either<ContractStatusDetails>(ContractStatusDetails::class.java) {
    override fun apply(item: ContractStatusDetails): Type = when (item) {
        ContractStatusDetails.VERIFICATION,
        ContractStatusDetails.VERIFIED -> Type.SUCCESS

        ContractStatusDetails.CONTRACT_PROJECT,
        ContractStatusDetails.CONTRACT_PREPARATION,
        ContractStatusDetails.ACTIVE,
        ContractStatusDetails.APPROVED,
        ContractStatusDetails.SIGNED,
        ContractStatusDetails.CANCELLED,
        ContractStatusDetails.COMPLETE,
        ContractStatusDetails.UNSUCCESSFUL,
        ContractStatusDetails.ISSUED,
        ContractStatusDetails.APPROVEMENT,
        ContractStatusDetails.EXECUTION,
        ContractStatusDetails.EMPTY -> Type.FAIL
    }
}
