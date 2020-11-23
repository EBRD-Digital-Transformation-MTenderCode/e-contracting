package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.lot.LotId
import java.time.LocalDateTime

data class CreatedCANData(
    val token: Token,
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
