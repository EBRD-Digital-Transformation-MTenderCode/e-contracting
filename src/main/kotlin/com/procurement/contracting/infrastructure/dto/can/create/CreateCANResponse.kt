package com.procurement.contracting.infrastructure.dto.can.create

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.infrastructure.bind.JsonDateTimeDeserializer
import com.procurement.contracting.infrastructure.bind.JsonDateTimeSerializer
import java.time.LocalDateTime
import java.util.*

data class CreateCANResponse(
    @field:JsonProperty("token") @param:JsonProperty("token") val token: UUID,
    @field:JsonProperty("can") @param:JsonProperty("can") val can: CAN
) {
    data class CAN(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("awardId") @param:JsonProperty("awardId") val awardId: UUID?,

        @field:JsonProperty("lotId") @param:JsonProperty("lotId") val lotId: UUID,

        @JsonDeserialize(using = JsonDateTimeDeserializer::class)
        @JsonSerialize(using = JsonDateTimeSerializer::class)
        @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,
        @field:JsonProperty("status") @param:JsonProperty("status") val status: CANStatus,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: CANStatusDetails
    )
}
