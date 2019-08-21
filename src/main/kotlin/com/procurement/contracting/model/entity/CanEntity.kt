package com.procurement.contracting.model.entity

import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.lot.LotId
import java.util.*

data class CanEntity(

    val cpId: String,

    val canId: CANId,

    val token: UUID,

    val owner: String,

    val createdDate: Date,

    val awardId: AwardId?,

    val lotId: LotId,

    var acId: String?,

    var status: String,

    var statusDetails: String,

    var jsonData: String
)
