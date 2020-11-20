package com.procurement.contracting.infrastructure.handler.v1.model.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.lot.LotId
import java.time.LocalDateTime

data class CreateCANResponse(
    @field:JsonProperty("token") @param:JsonProperty("token") val token: Token,
    @field:JsonProperty("can") @param:JsonProperty("can") val can: CAN
) {
    data class CAN(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: CANId,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("awardId") @param:JsonProperty("awardId") val awardId: AwardId?,

        @field:JsonProperty("lotId") @param:JsonProperty("lotId") val lotId: LotId,
        @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,
        @field:JsonProperty("status") @param:JsonProperty("status") val status: CANStatus,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: CANStatusDetails
    )
}
