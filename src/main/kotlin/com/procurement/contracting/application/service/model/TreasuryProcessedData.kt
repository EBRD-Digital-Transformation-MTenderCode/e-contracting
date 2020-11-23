package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.ac.id.AwardContractId
import com.procurement.contracting.domain.model.ac.status.AwardContractStatus
import com.procurement.contracting.domain.model.ac.status.AwardContractStatusDetails
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestReleaseTo
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestSource
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestType
import com.procurement.contracting.domain.model.confirmation.response.ConfirmationResponseType
import com.procurement.contracting.domain.model.document.type.DocumentTypeContract
import com.procurement.contracting.domain.model.item.ItemId
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.domain.model.milestone.status.MilestoneStatus
import com.procurement.contracting.domain.model.milestone.type.MilestoneType
import com.procurement.contracting.domain.model.organization.OrganizationId
import java.math.BigDecimal
import java.time.LocalDateTime

data class TreasuryProcessedData(
    val contract: AwardContract,
    val cans: List<Can>?
) {

    data class AwardContract(
        val id: AwardContractId,
        val date: LocalDateTime,
        val awardId: AwardId,
        val status: AwardContractStatus,
        val statusDetails: AwardContractStatusDetails,
        val title: String,
        val description: String,
        val period: Period,
        val documents: List<Document>,
        val milestones: List<Milestone>,
        val confirmationRequests: List<ConfirmationRequest>,
        val confirmationResponses: List<ConfirmationResponse>,
        val value: Value
    ) {
        data class Period(
            val startDate: LocalDateTime,
            val endDate: LocalDateTime
        )

        data class Document(
            val documentType: DocumentTypeContract,
            val id: String,
            val title: String?,
            val description: String?,
            val relatedLots: List<LotId>?,
            val relatedConfirmations: List<String>?
        )

        data class Milestone(
            val id: String,
            val relatedItems: List<ItemId>?,
            val status: MilestoneStatus,
            val additionalInformation: String?,
            val dueDate: LocalDateTime?,
            val title: String,
            val type: MilestoneType,
            val description: String,
            val dateModified: LocalDateTime?,
            val dateMet: LocalDateTime?,
            val relatedParties: List<RelatedParty>
        ) {
            data class RelatedParty(
                val id: OrganizationId,
                val name: String
            )
        }

        data class ConfirmationRequest(
            val id: String,
            val type: ConfirmationRequestType,
            val title: String,
            val description: String,
            val relatesTo: ConfirmationRequestReleaseTo,
            val relatedItem: String,
            val source: ConfirmationRequestSource,
            val requestGroups: List<RequestGroup>
        ) {

            data class RequestGroup(
                val id: String,
                val requests: List<Request>
            ) {

                data class Request(
                    val id: String,
                    val title: String,
                    val description: String,
                    val relatedPerson: RelatedPerson?
                ) {

                    data class RelatedPerson(
                        val id: String,
                        val name: String
                    )
                }
            }
        }

        data class ConfirmationResponse(
            val id: String,
            val value: Value,
            val request: String
        ) {

            data class Value(
                val id: OrganizationId,
                val name: String,
                val date: LocalDateTime,
                val relatedPerson: RelatedPerson?,
                val verifications: List<Verification>
            ) {

                data class RelatedPerson(
                    val id: String,
                    val name: String
                )

                data class Verification(
                    val type: ConfirmationResponseType,
                    val value: String,
                    val rationale: String?
                )
            }
        }

        data class Value(
            val amount: BigDecimal,
            val currency: String,
            val amountNet: BigDecimal,
            val valueAddedTaxIncluded: Boolean
        )
    }

    data class Can(
        val id: CANId,
        val status: CANStatus,
        val statusDetails: CANStatusDetails
    )
}
