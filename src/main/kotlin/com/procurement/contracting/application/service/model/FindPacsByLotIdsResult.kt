package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.DynamicValue
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.fc.Pac
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.pac.PacStatus
import java.time.LocalDateTime

data class FindPacsByLotIdsResult(
    val contracts: List<Contract>
) {
    data class Contract(
        val id: PacId,
        val status: PacStatus,
        val date: LocalDateTime,
        val relatedLots: List<LotId>,
        val suppliers: List<Supplier>,
        val awardId: AwardId?,
        val agreedMetrics: List<AgreedMetric>?
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
    }

    companion object {

        fun fromDomain(pac: Pac): Contract {
            return Contract(
                id = pac.id,
                status = pac.status,
                date = pac.date,
                relatedLots = pac.relatedLots.map { it },
                awardId = pac.awardId,
                suppliers = pac.suppliers.map { supplier -> supplier.convert() },
                agreedMetrics = pac.agreedMetrics.map { agreedMetric -> agreedMetric.convert() }
            )
        }

        private fun Pac.Supplier.convert(): Contract.Supplier =
            Contract.Supplier(
                id = id,
                name = name
            )

        private fun Pac.AgreedMetric.convert(): Contract.AgreedMetric =
            Contract.AgreedMetric(
                id = id,
                title = title,
                observations = observations.map { observation -> observation.convert() }
            )

        private fun Pac.AgreedMetric.Observation.convert(): Contract.AgreedMetric.Observation =
            Contract.AgreedMetric.Observation(
                id = id,
                notes = notes,
                measure = measure,
                relatedRequirementId = relatedRequirementId,
                period = period?.convert(),
                unit = unit?.convert()
            )

        private fun Pac.AgreedMetric.Observation.Period.convert(): Contract.AgreedMetric.Observation.Period =
            Contract.AgreedMetric.Observation.Period(
                startDate = startDate,
                endDate = endDate
            )

        private fun Pac.AgreedMetric.Observation.Unit.convert(): Contract.AgreedMetric.Observation.Unit =
            Contract.AgreedMetric.Observation.Unit(
                id = id,
                name = name
            )
    }
}