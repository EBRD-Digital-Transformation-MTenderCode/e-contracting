package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.lot.LotId
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Can @JsonCreator constructor(

    val id: CANId,

    val token: Token,

    var date: LocalDateTime?,

    val awardId: AwardId?,

    val lotId: LotId,

    var status: CANStatus,

    var statusDetails: CANStatusDetails,

    var documents: List<DocumentContract>?,

    var amendment: Amendment?
)
