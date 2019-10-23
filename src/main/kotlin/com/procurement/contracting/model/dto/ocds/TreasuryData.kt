package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class TreasuryData(
        @field:JsonProperty("externalRegId") @param:JsonProperty("externalRegId") val externalRegId: String,
        @field:JsonProperty("regDate") @param:JsonProperty("regDate") val regDate: String,
        @field:JsonProperty("dateMet") @param:JsonProperty("dateMet") val dateMet: LocalDateTime
)