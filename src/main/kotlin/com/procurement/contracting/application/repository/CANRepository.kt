package com.procurement.contracting.application.repository

import com.procurement.contracting.domain.entity.CANEntity
import com.procurement.contracting.domain.functional.Result
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.infrastructure.fail.Fail

interface CANRepository {
    fun findBy(cpid: String, canId: CANId): CANEntity?
    fun findBy(cpid: String): List<CANEntity>

    fun saveNewCAN(cpid: String, entity: CANEntity)

    fun saveCancelledCANs(cpid: String, dataCancelledCAN: DataCancelCAN, dataRelatedCANs: List<DataRelatedCAN>)

    fun updateStatusesCANs(cpid: String, cans: List<DataStatusesCAN>)

    fun resetCANs(cpid: String, cans: List<DataResetCAN>)

    fun relateContract(cpid: String, cans: List<RelatedContract>)

    fun tryFindBy(cpid: Cpid): Result<List<CANEntity>, Fail.Incident>
}

data class DataCancelCAN(
    val id: CANId,
    val status: CANStatus,
    val statusDetails: CANStatusDetails,
    val jsonData: String
)

data class DataResetCAN(
    val id: CANId,
    val status: CANStatus,
    val statusDetails: CANStatusDetails,
    val jsonData: String
)

data class DataRelatedCAN(
    val id: CANId,
    val status: CANStatus,
    val statusDetails: CANStatusDetails,
    val jsonData: String
)

data class DataStatusesCAN(
    val id: CANId,
    val status: CANStatus,
    val statusDetails: CANStatusDetails,
    val jsonData: String
)

data class RelatedContract(
    val id: CANId,
    val contractId: String,
    val status: CANStatus,
    val statusDetails: CANStatusDetails,
    val jsonData: String
)