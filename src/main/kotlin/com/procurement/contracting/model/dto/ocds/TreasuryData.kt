package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class TreasuryData(
    @field:JsonProperty("reg_nom") @param:JsonProperty("reg_nom") val regNom: String,
    @field:JsonProperty("reg_date") @param:JsonProperty("reg_date") val regDate: String,
    @field:JsonProperty("dateMet") @param:JsonProperty("dateMet") val dateMet: LocalDateTime
)