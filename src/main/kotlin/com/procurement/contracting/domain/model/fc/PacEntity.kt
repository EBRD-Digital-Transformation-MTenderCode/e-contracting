package com.procurement.contracting.domain.model.fc

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.DynamicValue
import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.pac.PacStatus
import com.procurement.contracting.domain.model.pac.PacStatusDetails
import com.procurement.contracting.domain.util.extension.toLocalDateTime

data class PacEntity(
    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
    @param:JsonProperty("owner") @field:JsonProperty("owner") val owner: String,
    @param:JsonProperty("status") @field:JsonProperty("status") val status: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: String?,

    @param:JsonProperty("date") @field:JsonProperty("date") val date: String,
    @param:JsonProperty("relatedLots") @field:JsonProperty("relatedLots") val relatedLots: List<String>,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @param:JsonProperty("suppliers") @field:JsonProperty("suppliers") val suppliers: List<Supplier> = emptyList(),

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @param:JsonProperty("awardId") @field:JsonProperty("awardId") val awardId: String? = null,

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
                @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: String,
                @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: String
            )

            data class Unit(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String
            )
        }
    }

    fun toDomain(): Pac {
        return Pac(
            id = PacId.orNull(id)!!,
            owner = Owner.orNull(owner)!!,
            statusDetails = statusDetails?.let { PacStatusDetails.orNull(it) },
            status = PacStatus.orNull(status)!!,
            date = date.toLocalDateTime().orThrow { it.reason },
            relatedLots = relatedLots.map { LotId.orNull(it)!! },
            awardId = awardId?.let { AwardId.orNull(it) },
            suppliers = suppliers.map { supplier -> supplier.convert() },
            agreedMetrics = agreedMetrics.map { agreedMetric -> agreedMetric.convert() }
        )
    }

    private fun Supplier.convert(): Pac.Supplier =
        Pac.Supplier(
            id = id,
            name = name
        )

    private fun AgreedMetric.convert(): Pac.AgreedMetric =
        Pac.AgreedMetric(
            id = id,
            title = title,
            observations = observations.map { observation -> observation.convert() }
        )

    private fun AgreedMetric.Observation.convert(): Pac.AgreedMetric.Observation =
        Pac.AgreedMetric.Observation(
            id = id,
            notes = notes,
            measure = measure,
            relatedRequirementId = relatedRequirementId,
            period = period?.convert(),
            unit = unit?.convert()
        )

    private fun AgreedMetric.Observation.Period.convert(): Pac.AgreedMetric.Observation.Period =
        Pac.AgreedMetric.Observation.Period(
            startDate = startDate.toLocalDateTime().orThrow { it.reason },
            endDate = endDate.toLocalDateTime().orThrow { it.reason }
        )

    private fun AgreedMetric.Observation.Unit.convert(): Pac.AgreedMetric.Observation.Unit =
        Pac.AgreedMetric.Observation.Unit(
            id = id,
            name = name
        )
}

