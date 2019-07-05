package com.procurement.contracting.infrastructure.dto.treasury

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.contracting.domain.model.treasury.TreasureResponseStatus
import com.procurement.contracting.infrastructure.bind.JsonDateTimeDeserializer
import com.procurement.contracting.infrastructure.bind.JsonDateTimeSerializer
import java.time.LocalDateTime

data class TreasureProcessingRequest(
    @field:JsonProperty("verification") @param:JsonProperty("verification") val verification: Verification,

    @JsonDeserialize(using = JsonDateTimeDeserializer::class)
    @JsonSerialize(using = JsonDateTimeSerializer::class)
    @field:JsonProperty("dateMet") @param:JsonProperty("dateMet") val dateMet: LocalDateTime,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("regData") @param:JsonProperty("regData") val regData: RegData?
) {

    data class Verification(
        @field:JsonProperty("value") @param:JsonProperty("value") val status: TreasureResponseStatus,
        @field:JsonProperty("rationale") @param:JsonProperty("rationale") val rationale: String
    )

    data class RegData(
        @field:JsonProperty("reg_nom") @param:JsonProperty("reg_nom") val regNom: String,
        @field:JsonProperty("reg_date") @param:JsonProperty("reg_date") val regDate: String
    )
}