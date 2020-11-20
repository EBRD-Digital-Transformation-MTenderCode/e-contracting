package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.ProcurementMethod
import com.procurement.contracting.domain.model.process.Cpid
import java.time.LocalDateTime

data class CreateACContext(
    val cpid: Cpid,
    val owner: Owner,
    val startDate: LocalDateTime,
    val pmd: ProcurementMethod,
    val language: String
)
