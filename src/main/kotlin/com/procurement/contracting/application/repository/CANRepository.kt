package com.procurement.contracting.application.repository

import com.procurement.contracting.domain.entity.CANEntity
import com.procurement.contracting.model.dto.ocds.ContractStatus
import com.procurement.contracting.model.dto.ocds.ContractStatusDetails
import java.util.*

interface CANRepository {
    fun findBy(cpid: String, canId: UUID): CANEntity?
    fun findBy(cpid: String): List<CANEntity>

    fun saveCancelledCANs(cpid: String, dataCancelledCAN: DataCancelCAN, dataRelatedCANs: List<DataCancelCAN>)
}

data class DataCancelCAN(
    val id: UUID,
    val status: ContractStatus,
    val statusDetails: ContractStatusDetails,
    val jsonData: String
)