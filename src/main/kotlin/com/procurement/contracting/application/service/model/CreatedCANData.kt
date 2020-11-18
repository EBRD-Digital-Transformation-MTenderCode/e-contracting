package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.lot.LotId
import java.time.LocalDateTime
import java.util.*

data class CreatedCANData(
    val token: UUID,
    val can: CAN
) {
    data class CAN(
        val id: CANId,
        val awardId: AwardId?,
        val lotId: LotId,
        val date: LocalDateTime,
        val status: CANStatus,
        val statusDetails: CANStatusDetails
    )
}
