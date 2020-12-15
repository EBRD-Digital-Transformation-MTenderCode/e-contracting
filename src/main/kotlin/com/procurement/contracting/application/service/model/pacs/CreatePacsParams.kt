package com.procurement.contracting.application.service.model.pacs

import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.bid.BidId
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.domain.model.requirement.response.RequirementRsValue
import java.time.LocalDateTime

class CreatePacsParams(
    val cpid: Cpid,
    val ocid: Ocid,
    val tender: Tender,
    val date: LocalDateTime,
    val awards: List<Award>,
    val bids: Bids?,
    val owner: Owner
) {
    data class Tender(
        val lots: List<Lot>,
        val targets: List<Target>,
        val criteria: List<Criteria>
    ) {
        data class Lot(
            val id: LotId
        )

        data class Target(
            val id: String,
            val observations: List<Observation>
        ) {
            data class Observation(
                val id: String,
                val unit: Unit,
                val relatedRequirementId: String?
            ) {
                data class Unit(
                    val id: String,
                    val name: String
                )
            }
        }

        data class Criteria(
            val id: String,
            val title: String,
            val relatesTo: String,
            val relatedItem: String?,
            val requirementGroups: List<RequirementGroup>
        ) {
            data class RequirementGroup(
                val id: String,
                val requirements: List<Requirement>
            ) {
                data class Requirement(
                    val id: String,
                    val title: String
                )
            }
        }
    }

    data class Award(
        val id: AwardId,
        val suppliers: List<Supplier>
    ) {
        data class Supplier(
            val id: String,
            val name: String
        )
    }

    data class Bids(
        val details: List<Detail>
    ) {
        data class Detail(
            val id: BidId,
            val tenderers: List<Tenderer>,
            val requirementResponses: List<RequirementResponse>?
        ) {
            data class Tenderer(
                val id: String,
                val name: String
            )

            data class RequirementResponse(
                val id: String,
                val value: RequirementRsValue,
                val requirement: Requirement,
                val period: Period?
            ) {
                data class Requirement(
                    val id: String
                )

                data class Period(
                    val startDate: LocalDateTime,
                    val endDate: LocalDateTime
                )
            }
        }
    }
}