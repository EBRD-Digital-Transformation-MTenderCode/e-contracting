package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.ProcurementMethodDetails
import com.procurement.contracting.domain.model.process.Cpid
import java.time.LocalDateTime

data class CreateAwardContractContext(
    val cpid: Cpid,
    val owner: Owner,
    val startDate: LocalDateTime,
    val pmd: ProcurementMethodDetails,
    val language: String
)
