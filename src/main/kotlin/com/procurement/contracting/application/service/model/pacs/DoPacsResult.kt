package com.procurement.contracting.application.service.model.pacs

import com.procurement.contracting.domain.model.DynamicValue
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.pac.PacStatus
import java.time.LocalDateTime

data class DoPacsResult(
    val contracts: List<Contract>
) {
    data class Contract(
        val id: PacId,
        val status: PacStatus,
        val date: LocalDateTime,
        val token: Token,
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
}