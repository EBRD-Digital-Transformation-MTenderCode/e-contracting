package com.procurement.contracting.application.service.can

import com.procurement.contracting.domain.model.lot.LotId
import java.time.LocalDateTime

data class CreateCANContext(
    val cpid: String,
    val ocid: String,
    val owner: String,
    val startDate: LocalDateTime,
    val lotId: LotId
)
