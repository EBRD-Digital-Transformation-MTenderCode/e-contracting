package com.procurement.contracting.domain.model.fc

import com.procurement.contracting.domain.model.DynamicValue
import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.pac.PacStatus
import com.procurement.contracting.domain.model.pac.PacStatusDetails
import com.procurement.contracting.domain.util.extension.asString
import java.time.LocalDateTime

data class Pac(
    val id: PacId,
    val owner: Owner,
    val token: Token,
    val status: PacStatus,
    val statusDetails: PacStatusDetails?,
    val date: LocalDateTime,
    val relatedLots: List<LotId>,
    val suppliers: List<Supplier> = emptyList(),
    val awardId: AwardId? = null,
    val agreedMetrics: List<AgreedMetric> = emptyList()
) {
    data class Supplier(
        val id: String,
        val name: String
    )

    data class AgreedMetric(
        val id: String,
        val title: String,
        val observations: List<Observation>
    ) {
        data class Observation(
            val id: String,
            val notes: String,
            val measure: DynamicValue,
            val relatedRequirementId: String,
            val period: Period?,
            val unit: Unit?
        ) {
            data class Period(
                val startDate: LocalDateTime,
                val endDate: LocalDateTime
            )

            data class Unit(
                val id: String,
                val name: String
            )
        }
    }

    fun toEntity(): PacEntity {
        return PacEntity(
            id = id.underlying,
            owner = owner.underlying,
            token = token.underlying.toString(),
            statusDetails = statusDetails?.key,
            status = status.key,
            date = date.asString(),
            relatedLots = relatedLots.map { it.underlying },
            awardId = awardId.toString(),
            suppliers = suppliers.map { supplier -> supplier.convert() },
            agreedMetrics = agreedMetrics.map { agreedMetric -> agreedMetric.convert() }
        )
    }

    private fun Supplier.convert(): PacEntity.Supplier =
        PacEntity.Supplier(
            id = id,
            name = name
        )

    private fun AgreedMetric.convert(): PacEntity.AgreedMetric =
        PacEntity.AgreedMetric(
            id = id,
            title = title,
            observations = observations.map { observation -> observation.convert() }
        )

    private fun AgreedMetric.Observation.convert(): PacEntity.AgreedMetric.Observation =
        PacEntity.AgreedMetric.Observation(
            id = id,
            notes = notes,
            measure = measure,
            relatedRequirementId = relatedRequirementId,
            period = period?.convert(),
            unit = unit?.convert()
        )

    private fun AgreedMetric.Observation.Period.convert(): PacEntity.AgreedMetric.Observation.Period =
        PacEntity.AgreedMetric.Observation.Period(
            startDate = startDate.asString(),
            endDate = endDate.asString()
        )

    private fun AgreedMetric.Observation.Unit.convert(): PacEntity.AgreedMetric.Observation.Unit =
        PacEntity.AgreedMetric.Observation.Unit(
            id = id,
            name = name
        )
}

