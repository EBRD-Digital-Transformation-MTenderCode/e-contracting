package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.lot.LotId

data class ConfirmationCanRs @JsonCreator constructor(

    val cans: List<ConfirmationCan>,

    val lotId: LotId
)

data class ConfirmationCan @JsonCreator constructor(

    val id: String,

    var status: CANStatus,

    var statusDetails: CANStatusDetails
)