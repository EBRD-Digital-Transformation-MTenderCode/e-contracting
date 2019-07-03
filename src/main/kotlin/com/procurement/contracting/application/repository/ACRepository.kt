package com.procurement.contracting.application.repository

import com.procurement.contracting.domain.entity.ACEntity
import com.procurement.contracting.domain.model.contract.status.ContractStatus
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails

interface ACRepository {
    fun findBy(cpid: String, contractId: String): ACEntity?

    fun saveCancelledAC(dataCancelledAC: DataCancelledAC)
}

data class DataCancelledAC(
    val id: String,
    val cpid: String,
    val status: ContractStatus,
    val statusDetails: ContractStatusDetails,
    val jsonData: String
)