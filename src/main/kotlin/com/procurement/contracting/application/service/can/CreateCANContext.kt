package com.procurement.contracting.application.service.can

import java.time.LocalDateTime
import java.util.*

data class CreateCANContext(
    val cpid: String,
    val ocid: String,
    val owner: String,
    val startDate: LocalDateTime,
    val lotId: UUID
)
