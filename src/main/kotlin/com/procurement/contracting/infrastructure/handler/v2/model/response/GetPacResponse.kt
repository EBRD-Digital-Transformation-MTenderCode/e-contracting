package com.procurement.contracting.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.DynamicValue
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.fc.Pac
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.pac.PacStatus
import com.procurement.contracting.domain.model.pac.PacStatusDetails
import java.time.LocalDateTime

data class GetPacResponse(
    @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>
) {
    data class Contract(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
        @param:JsonProperty("status") @field:JsonProperty("status") val status: PacStatus,
        @param:JsonProperty("relatedLots") @field:JsonProperty("relatedLots") val relatedLots: List<LotId>,
        @param:JsonProperty("suppliers") @field:JsonProperty("suppliers") val suppliers: List<Supplier>,
        @param:JsonProperty("awardId") @field:JsonProperty("awardId") val awardId: AwardId,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("agreedMetrics") @field:JsonProperty("agreedMetrics") val agreedMetrics: List<AgreedMetric>?,
        @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: PacStatusDetails?
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

    object ResponseConverter {
        fun fromDomain(pacEntity: Pac) =
            GetPacResponse(
                Contract(
                    id = pacEntity.id.toString(),
                    status = pacEntity.status,
                    relatedLots = pacEntity.relatedLots,
                    suppliers = pacEntity.suppliers.map { fromDomain(it) },
                    awardId = pacEntity.awardId!!,
                    agreedMetrics = pacEntity.agreedMetrics.map { fromDomain(it) },
                    date = pacEntity.date,
                    statusDetails = pacEntity.statusDetails
                ).let { listOf(it) }
            )

        fun fromDomain(supplier: Pac.Supplier) =
            Contract.Supplier(
                id = supplier.id,
                name = supplier.name
            )

        fun fromDomain(agreedMetric: Pac.AgreedMetric) =
            Contract.AgreedMetric(
                id = agreedMetric.id,
                title = agreedMetric.title,
                observations = agreedMetric.observations.map { fromDomain(it) }
            )

        fun fromDomain(observation: Pac.AgreedMetric.Observation) =
            Contract.AgreedMetric.Observation(
                id = observation.id,
                notes = observation.notes,
                measure = observation.measure,
                relatedRequirementId = observation.relatedRequirementId,
                period = observation.period?.let { fromDomain(it) },
                unit = observation.unit?.let { fromDomain(it) }
            )

        fun fromDomain(period: Pac.AgreedMetric.Observation.Period) =
            Contract.AgreedMetric.Observation.Period(
                startDate = period.startDate,
                endDate = period.endDate
            )

        fun fromDomain(unit: Pac.AgreedMetric.Observation.Unit) =
            Contract.AgreedMetric.Observation.Unit(
                id = unit.id,
                name = unit.name
            )
    }
}