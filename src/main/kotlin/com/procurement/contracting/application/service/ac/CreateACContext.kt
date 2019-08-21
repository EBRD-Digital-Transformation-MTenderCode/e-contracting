package com.procurement.contracting.application.service.ac

import com.procurement.contracting.domain.model.ProcurementMethod
import java.time.LocalDateTime

data class CreateACContext(
    val cpid: String,
    val owner: String,
    val startDate: LocalDateTime,
    val pmd: ProcurementMethod,
    val language: String
)
