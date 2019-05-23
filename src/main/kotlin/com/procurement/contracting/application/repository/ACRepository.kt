package com.procurement.contracting.application.repository

import com.procurement.contracting.domain.entity.ACEntity
import com.procurement.contracting.model.dto.ocds.ContractStatus
import com.procurement.contracting.model.dto.ocds.ContractStatusDetails

interface ACRepository {
    fun findBy(cpid: String, contractId: String): ACEntity?

    fun cancellationAC(dataCancelAC: DataCancelAC)
}

data class DataCancelAC(
    val id: String,
    val cpid: String,
    val status: ContractStatus,
    val statusDetails: ContractStatusDetails,
    val jsonData: String
)