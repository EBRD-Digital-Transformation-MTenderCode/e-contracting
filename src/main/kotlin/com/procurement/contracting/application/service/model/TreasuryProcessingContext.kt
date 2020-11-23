package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import java.time.LocalDateTime

data class TreasuryProcessingContext(
    val cpid: Cpid,
    val ocid: Ocid,
    val startDate: LocalDateTime
)
