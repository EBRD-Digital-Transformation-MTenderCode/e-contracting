package com.procurement.contracting.domain.entity

import java.time.LocalDateTime
import java.util.*

data class ACEntity(
    val cpid: String,
    var id: String,
    val token: UUID,
    val owner: String,
    val createdDate: LocalDateTime,
    var status: String,
    var statusDetails: String,
    val mainProcurementCategory: String,
    val language: String,
    var jsonData: String
)