package com.procurement.contracting.application.service

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.contracting.AbstractArgumentConverter
import com.procurement.contracting.application.repository.ac.ACRepository
import com.procurement.contracting.application.repository.can.CANRepository
import com.procurement.contracting.application.repository.model.ContractProcess
import com.procurement.contracting.domain.entity.ACEntity
import com.procurement.contracting.domain.entity.CANEntity
import com.procurement.contracting.domain.model.MainProcurementCategory
import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.can.CAN
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.contract.status.ContractStatus
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails
import com.procurement.contracting.domain.model.document.type.DocumentTypeAmendment
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.json.loadJson
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.converter.ConvertWith
import org.junit.jupiter.params.provider.CsvSource
import java.util.*

class CancelCANServiceTest {
    companion object {
        private val CPID = Cpid.orNull("ocds-b3wdp1-MD-1580458690892")!!
        private val CAN_TOKEN: Token = Token.orNull("2909bc16-82c7-4281-8f35-3f0bb13476b8")!!
        private val OWNER: Owner = Owner.orNull("d0da4c24-1a2a-4b39-a1fd-034cb887c93b")!!
        private val CAN_ID: CANId = UUID.fromString("0dc181db-f5ae-4039-97c7-defcceef89a4")
        private val LOT_ID: LotId = LotId.orNull("f02720a6-de85-4a50-aa3d-e9348f1669dc")!!
        private const val CONTRACT_ID: String = "contract-id-1"
        private val MPC = MainProcurementCategory.SERVICES

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

    @ParameterizedTest(name = "CAN - status: ''{0}'' & status details: ''{1}''")
    @CsvSource(
        "pending, contractProject",
        "pending, active",
        "pending, unsuccessful"
    )
    fun cancelCANWithoutContract(
        @ConvertWith(CANStatusConverter::class) canStatus: CANStatus,
        @ConvertWith(CANStatusDetailsConverter::class) canStatusDetails: CANStatusDetails
    ) {
        val cancellationCANEntity = canEntity(
            can = cancellationCAN.copy(
                status = canStatus,
                statusDetails = canStatusDetails
            ),
            contractID = null
        )

        whenever(canRepository.findBy(eq(CPID), eq(CAN_ID)))
            .thenReturn(cancellationCANEntity.asSuccess())
        whenever(acRepository.findBy(eq(CPID), eq(CONTRACT_ID)))
            .thenReturn(null)
        whenever(canRepository.findBy(eq(CPID)))
            .thenReturn(listOf(cancellationCANEntity).asSuccess())
        whenever(canRepository.saveCancelledCANs(eq(CPID), any(), any()))
            .thenReturn(true.asSuccess())

        val data = data()
        val response = service.cancel(context = context(), data = data)

        val canceledCAN = response.cancelledCAN
        assertEquals(cancellationCANEntity.id, canceledCAN.id)
        assertEquals(CANStatus.CANCELLED, canceledCAN.status)
        assertEquals(CANStatusDetails.EMPTY, canceledCAN.statusDetails)

        assertNotNull(canceledCAN.amendment)
        val amendment = canceledCAN.amendment
        assertEquals(data.amendment.rationale, amendment.rationale)
        assertEquals(data.amendment.description, amendment.description)
        assertEquals(data.amendment.documents!!.size, amendment.documents!!.size)

        val documents = amendment.documents!!
        assertEquals(data.amendment.documents!![0].id, documents[0].id)
        assertEquals(data.amendment.documents!![0].documentType, documents[0].documentType)
        assertEquals(data.amendment.documents!![0].title, documents[0].title)
        assertEquals(data.amendment.documents!![0].description, documents[0].description)

        assertEquals(LOT_ID, response.lotId)

        assertNull(response.contract)

        verify(acRepository, times(0)).saveCancelledAC(any(), any(), any(), any(), any())
        verify(canRepository, times(1)).saveCancelledCANs(any(), any(), any())
    }

    @ParameterizedTest(name = "Contract - status: ''{0}'' & statusDetails: ''{1}''")
    @CsvSource(
        "pending, contractProject",
        "pending, contractPreparation",
        "pending, approved",
        "pending, signed",
        "pending, issued",
        "pending, approvement",
        "pending, execution",
        "pending, empty",
        "cancelled, empty"
    )
    fun cancelCANWithContractWithoutRelatedCANs(
        @ConvertWith(ContractStatusConverter::class) contractStatus: ContractStatus,
        @ConvertWith(ContractStatusDetailsConverter::class) contractStatusDetails: ContractStatusDetails
    ) {
        val cancellationCANEntity = canEntity(can = cancellationCAN)
        whenever(canRepository.findBy(eq(CPID), eq(CAN_ID)))
            .thenReturn(cancellationCANEntity.asSuccess())

        val acEntity = acEntity(
            contractProcess = contractProcess.copy(
                contract = contractProcess.contract.copy(
                    status = contractStatus,
                    statusDetails = contractStatusDetails
                )
            )
        )
        whenever(acRepository.findBy(eq(CPID), eq(CONTRACT_ID)))
            .thenReturn(acEntity.asSuccess())

        whenever(canRepository.findBy(eq(CPID)))
            .thenReturn(listOf(cancellationCANEntity).asSuccess())

        whenever(acRepository.saveCancelledAC(eq(CPID), any(), any(), any(), any()))
            .thenReturn(true.asSuccess())

        whenever(canRepository.saveCancelledCANs(eq(CPID), any(), any()))
            .thenReturn(true.asSuccess())

        val data = data()
        val response = service.cancel(context = context(), data = data)

        val canceledCAN = response.cancelledCAN
        assertEquals(cancellationCANEntity.id, canceledCAN.id)
        assertEquals(CANStatus.CANCELLED, canceledCAN.status)
        assertEquals(CANStatusDetails.EMPTY, canceledCAN.statusDetails)

        assertNotNull(canceledCAN.amendment)
        val amendment = canceledCAN.amendment
        assertEquals(data.amendment.rationale, amendment.rationale)
        assertEquals(data.amendment.description, amendment.description)
        assertEquals(data.amendment.documents!!.size, amendment.documents!!.size)

        val documents = amendment.documents!!
        assertEquals(data.amendment.documents!![0].id, documents[0].id)
        assertEquals(data.amendment.documents!![0].documentType, documents[0].documentType)
        assertEquals(data.amendment.documents!![0].title, documents[0].title)
        assertEquals(data.amendment.documents!![0].description, documents[0].description)

        assertEquals(LOT_ID, response.lotId)

        assertNotNull(response.contract)
        val contract = response.contract!!
        assertEquals(CONTRACT_ID, contract.id)
        assertEquals(ContractStatus.CANCELLED, contract.status)
        assertEquals(ContractStatusDetails.EMPTY, contract.statusDetails)

        verify(acRepository, times(1)).saveCancelledAC(any(), any(), any(), any(), any())
        verify(canRepository, times(1)).saveCancelledCANs(any(), any(), any())
    }

    @Test
    fun cancelCANWithContractWithRelatedCAN() {
        val cancellationCANEntity = canEntity(can = cancellationCAN)
        whenever(canRepository.findBy(eq(CPID), eq(CAN_ID)))
            .thenReturn(cancellationCANEntity.asSuccess())

        val acEntity = acEntity(contractProcess = contractProcess)
        whenever(acRepository.findBy(eq(CPID), eq(CONTRACT_ID)))
            .thenReturn(acEntity.asSuccess())

        val firstCANEntity = canEntity(can = firstOtherCAN)
        val secondCANEntity = canEntity(can = secondOtherCAN, contractID = "UNKNOWN")
        whenever(canRepository.findBy(eq(CPID)))
            .thenReturn(listOf(cancellationCANEntity, firstCANEntity, secondCANEntity).asSuccess())
        whenever(acRepository.saveCancelledAC(eq(CPID), any(), any(), any(), any()))
            .thenReturn(true.asSuccess())
        whenever(canRepository.saveCancelledCANs(eq(CPID), any(), any()))
            .thenReturn(true.asSuccess())

        val data = data()
        val response = service.cancel(context = context(), data = data)

        assertEquals(LOT_ID, response.lotId)

        assertNotNull(response.contract)
        val contract = response.contract!!
        assertEquals(CONTRACT_ID, contract.id)
        assertEquals(ContractStatus.CANCELLED, contract.status)
        assertEquals(ContractStatusDetails.EMPTY, contract.statusDetails)

        val cancelledCAN = response.cancelledCAN
        assertEquals(cancellationCAN.id, cancelledCAN.id)
        assertEquals(CANStatus.CANCELLED, cancelledCAN.status)
        assertEquals(CANStatusDetails.EMPTY, cancelledCAN.statusDetails)

        assertNotNull(cancelledCAN.amendment)
        val amendment = cancelledCAN.amendment
        assertEquals(data.amendment.rationale, amendment.rationale)
        assertEquals(data.amendment.description, amendment.description)
        assertEquals(data.amendment.documents!!.size, amendment.documents!!.size)

        val documents = amendment.documents!!
        assertEquals(data.amendment.documents!![0].id, documents[0].id)
        assertEquals(data.amendment.documents!![0].documentType, documents[0].documentType)
        assertEquals(data.amendment.documents!![0].title, documents[0].title)
        assertEquals(data.amendment.documents!![0].description, documents[0].description)

        assertEquals(1, response.relatedCANs.size)
        val firstRelatedCAN = response.relatedCANs[0]
        assertEquals(firstOtherCAN.id, firstRelatedCAN.id)
        assertEquals(CANStatus.PENDING, firstRelatedCAN.status)
        assertEquals(CANStatusDetails.CONTRACT_PROJECT, firstRelatedCAN.statusDetails)

        verify(acRepository, times(1)).saveCancelledAC(any(), any(), any(), any(), any())
        verify(canRepository, times(1)).saveCancelledCANs(any(), any(), any())
    }

    @Test
    fun canNotFound() {
        whenever(canRepository.findBy(eq(CPID), eq(CAN_ID)))
            .thenReturn(Result.success(null))

        val exception = assertThrows<ErrorException> {
            service.cancel(context = context(), data = data())
        }

        assertEquals(ErrorType.CAN_NOT_FOUND, exception.error)
    }

    @Test
    fun invalidOwner() {
        val canEntity = canEntity(can = cancellationCAN, owner = Owner.orNull("00000000-0000-0000-0000-000000000000")!!)
        whenever(canRepository.findBy(eq(CPID), eq(CAN_ID)))
            .thenReturn(canEntity.asSuccess())

        val exception = assertThrows<ErrorException> {
            service.cancel(context = context(), data = data())
        }

        assertEquals(ErrorType.INVALID_OWNER, exception.error)
    }

    @Test
    fun invalidToken() {
        val canEntity = canEntity(can = cancellationCAN, token = Token.generate())
        whenever(canRepository.findBy(eq(CPID), eq(CAN_ID)))
            .thenReturn(canEntity.asSuccess())

        val exception = assertThrows<ErrorException> {
            service.cancel(context = context(), data = data())
        }

        assertEquals(ErrorType.INVALID_TOKEN, exception.error)
    }

    @Test
    fun contractNotFound() {
        val canEntity = canEntity(cancellationCAN)
        whenever(canRepository.findBy(eq(CPID), eq(CAN_ID)))
            .thenReturn(Result.success(canEntity))
        whenever(acRepository.findBy(eq(CPID), eq(canEntity.contractId!!)))
            .thenReturn(Result.success(null))

        val exception = assertThrows<ErrorException> {
            service.cancel(context = context(), data = data())
        }

        assertEquals(ErrorType.CONTRACT_NOT_FOUND, exception.error)
    }

    @ParameterizedTest(name = "CAN status: ''{0}''")
    @CsvSource(
        "active",
        "cancelled",
        "unsuccessful"
    )
    fun invalidCANStatus(@ConvertWith(CANStatusConverter::class) canStatus: CANStatus) {
        val cancellationCANEntity = canEntity(
            can = cancellationCAN.copy(
                status = canStatus
            ),
            contractID = null
        )
        whenever(canRepository.findBy(eq(CPID), eq(CAN_ID)))
            .thenReturn(cancellationCANEntity.asSuccess())

        whenever(acRepository.findBy(eq(CPID), eq(CONTRACT_ID)))
            .thenReturn(null)

        whenever(canRepository.findBy(eq(CPID)))
            .thenReturn(listOf(cancellationCANEntity).asSuccess())

        val exception = assertThrows<ErrorException> {
            service.cancel(context = context(), data = data())
        }

        assertEquals(ErrorType.INVALID_CAN_STATUS, exception.error)
    }

    @ParameterizedTest(name = "CAN - status: ''{0}'' & status details: ''{1}''")
    @CsvSource(
        "pending, empty",
        "pending, treasuryRejection"
    )
    fun invalidCANStatusDetails(
        @ConvertWith(CANStatusConverter::class) canStatus: CANStatus,
        @ConvertWith(CANStatusDetailsConverter::class) canStatusDetails: CANStatusDetails
    ) {
        val cancellationCANEntity = canEntity(
            can = cancellationCAN.copy(
                status = canStatus,
                statusDetails = canStatusDetails
            ),
            contractID = null
        )
        whenever(canRepository.findBy(eq(CPID), eq(CAN_ID)))
            .thenReturn(cancellationCANEntity.asSuccess())

        whenever(acRepository.findBy(eq(CPID), eq(CONTRACT_ID)))
            .thenReturn(null)

        whenever(canRepository.findBy(eq(CPID)))
            .thenReturn(listOf(cancellationCANEntity).asSuccess())

        val exception = assertThrows<ErrorException> {
            service.cancel(context = context(), data = data())
        }

        assertEquals(ErrorType.INVALID_CAN_STATUS_DETAILS, exception.error)
    }

    @ParameterizedTest(name = "Contract - status: ''{0}''")
    @CsvSource(
        "active",
        "complete",
        "terminated",
        "unsuccessful"
    )
    fun invalidContractStatus(@ConvertWith(ContractStatusConverter::class) contractStatus: ContractStatus) {
        val cancellationCANEntity = canEntity(can = cancellationCAN)
        whenever(canRepository.findBy(eq(CPID), eq(CAN_ID)))
            .thenReturn(cancellationCANEntity.asSuccess())

        val acEntity = acEntity(
            contractProcess.copy(
                contract = contractProcess.contract.copy(
                    status = contractStatus
                )
            )
        )
        whenever(acRepository.findBy(eq(CPID), eq(CONTRACT_ID)))
            .thenReturn(acEntity.asSuccess())

        whenever(canRepository.findBy(eq(CPID)))
            .thenReturn(listOf(cancellationCANEntity).asSuccess())

        val exception = assertThrows<ErrorException> {
            service.cancel(context = context(), data = data())
        }

        assertEquals(ErrorType.CONTRACT_STATUS, exception.error)
    }

    @ParameterizedTest(name = "Contract - status: ''{0}'' & status details: ''{1}''")
    @CsvSource(
        "pending, verification",
        "pending, verified",
        "cancelled, contractProject",
        "cancelled, contractPreparation",
        "cancelled, approved",
        "cancelled, signed",
        "cancelled, verification",
        "cancelled, verified",
        "cancelled, issued",
        "cancelled, approvement",
        "cancelled, execution"
    )
    fun invalidContractStatusDetails(
        @ConvertWith(ContractStatusConverter::class) contractStatus: ContractStatus,
        @ConvertWith(ContractStatusDetailsConverter::class) contractStatusDetails: ContractStatusDetails
    ) {
        val cancellationCANEntity = canEntity(can = cancellationCAN)
        whenever(canRepository.findBy(eq(CPID), eq(CAN_ID)))
            .thenReturn(cancellationCANEntity.asSuccess())

        val acEntity = acEntity(
            contractProcess.copy(
                contract = contractProcess.contract.copy(
                    status = contractStatus,
                    statusDetails = contractStatusDetails
                )
            )
        )
        whenever(acRepository.findBy(eq(CPID), eq(CONTRACT_ID)))
            .thenReturn(acEntity.asSuccess())

        whenever(canRepository.findBy(eq(CPID)))
            .thenReturn(listOf(cancellationCANEntity).asSuccess())

        val exception = assertThrows<ErrorException> {
            service.cancel(context = context(), data = data())
        }

        assertEquals(ErrorType.CONTRACT_STATUS_DETAILS, exception.error)
    }

    private fun context(
        cpid: Cpid = CPID,
        token: Token = CAN_TOKEN,
        owner: Owner = OWNER,
        canId: CANId = CAN_ID
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

    private fun canEntity(can: CAN, owner: Owner = OWNER, token: Token? = null, contractID: String? = CONTRACT_ID) =
        CANEntity(
            cpid = CPID,
            id = can.id,
            token = token ?: can.token,
            owner = owner,
            createdDate = can.date,
            awardId = can.awardId,
            lotId = can.lotId,
            contractId = contractID,
            status = can.status,
            statusDetails = can.statusDetails,
            jsonData = toJson(can)
        )

    private fun acEntity(contractProcess: ContractProcess) = ACEntity(
        cpid = CPID,
        id = contractProcess.contract.id,
        token = contractProcess.contract.token!!,
        owner = OWNER,
        createdDate = contractProcess.contract.date!!,
        status = contractProcess.contract.status,
        statusDetails = contractProcess.contract.statusDetails,
        mainProcurementCategory = MPC,
        language = "RO",
        jsonData = toJson(contractProcess)
    )
}

class CANStatusConverter : AbstractArgumentConverter<CANStatus>() {
    override fun converting(source: String): CANStatus = CANStatus.creator(source)
}

class CANStatusDetailsConverter : AbstractArgumentConverter<CANStatusDetails>() {
    override fun converting(source: String): CANStatusDetails = CANStatusDetails.creator(source)
}

class ContractStatusConverter : AbstractArgumentConverter<ContractStatus>() {
    override fun converting(source: String): ContractStatus = ContractStatus.creator(source)
}

class ContractStatusDetailsConverter : AbstractArgumentConverter<ContractStatusDetails>() {
    override fun converting(source: String): ContractStatusDetails = ContractStatusDetails.creator(source)
}
