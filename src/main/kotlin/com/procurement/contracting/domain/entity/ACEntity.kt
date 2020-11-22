package com.procurement.contracting.domain.entity

import com.procurement.contracting.domain.model.MainProcurementCategory
import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.contract.id.ContractId
import com.procurement.contracting.domain.model.contract.status.ContractStatus
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails
import com.procurement.contracting.domain.model.process.Cpid
import java.time.LocalDateTime

data class ACEntity(
    val cpid: Cpid,
    val id: ContractId,
    val token: Token,
    val owner: Owner,
    val createdDate: LocalDateTime,
    val status: ContractStatus,
    val statusDetails: ContractStatusDetails,
    val mainProcurementCategory: MainProcurementCategory,
    val language: String,
    val jsonData: String
)
