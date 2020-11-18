package com.procurement.contracting.application.repository.model

import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
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

    var status: CANStatus,

    var statusDetails: CANStatusDetails,

    var jsonData: String
)
