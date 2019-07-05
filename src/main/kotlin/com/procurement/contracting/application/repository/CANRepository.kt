package com.procurement.contracting.application.repository

import com.procurement.contracting.domain.entity.CANEntity
import com.procurement.contracting.domain.model.contract.status.ContractStatus
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails
import java.util.*

interface CANRepository {
    fun findBy(cpid: String, canId: UUID): CANEntity?
    fun findBy(cpid: String): List<CANEntity>

    fun saveCancelledCANs(cpid: String, dataCancelledCAN: DataCancelCAN, dataRelatedCANs: List<DataRelatedCAN>)

    fun updateStatusesCANs(cpid: String, cans: List<DataStatusesCAN>)
}

data class DataCancelCAN(
    val id: UUID,
    val status: ContractStatus,
    val statusDetails: ContractStatusDetails,
    val jsonData: String
)

data class DataRelatedCAN(
    val id: UUID,
    val status: ContractStatus,
    val statusDetails: ContractStatusDetails,
    val jsonData: String
)

data class DataStatusesCAN(
    val id: UUID,
    val status: ContractStatus,
    val statusDetails: ContractStatusDetails,
    val jsonData: String
)