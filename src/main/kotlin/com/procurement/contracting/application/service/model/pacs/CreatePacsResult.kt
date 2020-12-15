package com.procurement.contracting.application.service.model.pacs

import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.ac.status.AwardContractStatus
import com.procurement.contracting.domain.model.ac.status.AwardContractStatusDetails
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.lot.LotId
import java.time.LocalDateTime

data class CreatePacsResult(
    val contracts: List<Contract>,
    val token: Token?
) {
    data class Contract(
        val id: String,
        val status: AwardContractStatus,
        val statusDetails: AwardContractStatusDetails,
        val date: String,
        val relatedLots: List<LotId>,
        val suppliers: List<Supplier>?,
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
                val measure: String,
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