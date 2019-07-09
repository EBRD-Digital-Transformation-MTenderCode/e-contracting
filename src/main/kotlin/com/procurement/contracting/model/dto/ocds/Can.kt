package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Can @JsonCreator constructor(

        val id: String,

        val token: String,

        var date: LocalDateTime?,

        val awardId: String?,

        val lotId: String,

        var status: CANStatus,

        var statusDetails: CANStatusDetails,

        var documents: List<DocumentContract>?,

        var amendment: Amendment?
)
