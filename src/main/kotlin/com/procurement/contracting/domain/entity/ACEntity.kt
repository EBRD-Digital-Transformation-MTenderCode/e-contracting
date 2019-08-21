package com.procurement.contracting.domain.entity

import com.procurement.contracting.domain.model.MainProcurementCategory
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
    val mainProcurementCategory: MainProcurementCategory,
    val language: String,
    var jsonData: String
)