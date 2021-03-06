package com.procurement.contracting.application.repository.can.model

import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.ac.id.AwardContractId
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.domain.model.process.Cpid
import java.time.LocalDateTime

data class CANEntity(
    val cpid: Cpid,
    val id: CANId,
    val token: Token,
    val owner: Owner,
    val createdDate: LocalDateTime,
    val awardId: AwardId?,
    val lotId: LotId,
    val awardContractId: AwardContractId?,
    val status: CANStatus,
    val statusDetails: CANStatusDetails,
    val jsonData: String
)
