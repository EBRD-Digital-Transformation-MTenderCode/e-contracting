package com.procurement.contracting.domain.entity

import java.time.LocalDateTime
import java.util.*

data class CANEntity(
    val cpid: String,
    val id: UUID,
    val token: UUID,
    val owner: String,
    val createdDate: LocalDateTime,
    val awardId: String?,
    val lotId: String,
    val contractId: String?,
    val status: String,
    val statusDetails: String,
    val jsonData: String
)