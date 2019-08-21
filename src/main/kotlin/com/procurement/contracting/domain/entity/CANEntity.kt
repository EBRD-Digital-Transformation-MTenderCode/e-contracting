package com.procurement.contracting.domain.entity

import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.lot.LotId
import java.time.LocalDateTime
import java.util.*

data class CANEntity(
    val cpid: String,
    val id: CANId,
    val token: UUID,
    val owner: String,
    val createdDate: LocalDateTime,
    val awardId: AwardId?,
    val lotId: LotId,
    val contractId: String?,
    val status: String,
    val statusDetails: String,
    val jsonData: String
)
