package com.procurement.contracting.domain.model.fc

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.DynamicValue
import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.pac.PacStatus
import com.procurement.contracting.domain.model.pac.PacStatusDetails
import java.time.LocalDateTime

data class Pac(
    @param:JsonProperty("id") @field:JsonProperty("id") val id: PacId,
    @param:JsonProperty("token") @field:JsonProperty("token") val token: Token? = null,
    @param:JsonProperty("owner") @field:JsonProperty("owner") val owner: Owner,
    @param:JsonProperty("status") @field:JsonProperty("status") val status: PacStatus,
    @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: PacStatusDetails,
    @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime,
    @param:JsonProperty("relatedLots") @field:JsonProperty("relatedLots") val relatedLots: List<LotId>,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @param:JsonProperty("suppliers") @field:JsonProperty("suppliers") val suppliers: List<Supplier> = emptyList(),

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @param:JsonProperty("awardId") @field:JsonProperty("awardId") val awardId: AwardId? = null,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @param:JsonProperty("agreedMetrics ") @field:JsonProperty("agreedMetrics ") val agreedMetrics: List<AgreedMetric> = emptyList()
) {
    data class Supplier(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
        @param:JsonProperty("name") @field:JsonProperty("name") val name: String
    )

    data class AgreedMetric(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
        @param:JsonProperty("title") @field:JsonProperty("title") val title: String,
        @param:JsonProperty("observations") @field:JsonProperty("observations") val observations: List<Observation>
    ) {
        data class Observation(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("notes") @field:JsonProperty("notes") val notes: String,
            @param:JsonProperty("measure") @field:JsonProperty("measure") val measure: DynamicValue,
            @param:JsonProperty("relatedRequirementId") @field:JsonProperty("relatedRequirementId") val relatedRequirementId: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("period") @field:JsonProperty("period") val period: Period?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("unit") @field:JsonProperty("unit") val unit: Unit?
        ) {
            data class Period(
                @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime,
                @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime
            )

            data class Unit(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String
            )
        }
    }
}

