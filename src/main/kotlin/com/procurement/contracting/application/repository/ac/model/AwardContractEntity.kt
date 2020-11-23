package com.procurement.contracting.application.repository.ac.model

import com.procurement.contracting.domain.model.MainProcurementCategory
import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.ac.id.AwardContractId
import com.procurement.contracting.domain.model.ac.status.AwardContractStatus
import com.procurement.contracting.domain.model.ac.status.AwardContractStatusDetails
import com.procurement.contracting.domain.model.process.Cpid
import java.time.LocalDateTime

data class AwardContractEntity(
    val cpid: Cpid,
    val id: AwardContractId,
    val token: Token,
    val owner: Owner,
    val createdDate: LocalDateTime,
    val status: AwardContractStatus,
    val statusDetails: AwardContractStatusDetails,
    val mainProcurementCategory: MainProcurementCategory,
    val language: String,
    val jsonData: String
)
