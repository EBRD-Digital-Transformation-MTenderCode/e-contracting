package com.procurement.contracting.model.entity

import com.procurement.contracting.domain.model.award.AwardId
import java.util.*

data class CanEntity(

    val cpId: String,

    val canId: UUID,

    val token: UUID,

    val owner: String,

    val createdDate: Date,

    val awardId: AwardId?,

    val lotId: String,

    var acId: String?,

    var status: String,

    var statusDetails: String,

    var jsonData: String
)
