package com.procurement.contracting.infrastructure.dto.treasury

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestReleaseTo
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestSource
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestType
import com.procurement.contracting.domain.model.confirmation.response.ConfirmationResponseType
import com.procurement.contracting.domain.model.contract.status.ContractStatus
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails
import com.procurement.contracting.domain.model.document.type.DocumentTypeContract
import com.procurement.contracting.domain.model.milestone.status.MilestoneStatus
import com.procurement.contracting.domain.model.milestone.type.MilestoneType
import com.procurement.contracting.infrastructure.amount.AmountDeserializer
import com.procurement.contracting.infrastructure.amount.AmountSerializer
import com.procurement.contracting.infrastructure.bind.JsonDateTimeDeserializer
import com.procurement.contracting.infrastructure.bind.JsonDateTimeSerializer
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class TreasureProcessingResponse(
    @field:JsonProperty("contract") @param:JsonProperty("contract") val contract: Contract,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("cans") @param:JsonProperty("cans") val cans: List<Can>?
) {

    data class Contract(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

        @JsonDeserialize(using = JsonDateTimeDeserializer::class)
        @JsonSerialize(using = JsonDateTimeSerializer::class)
        @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,

        @field:JsonProperty("awardId") @param:JsonProperty("awardId") val awardId: AwardId,
        @field:JsonProperty("status") @param:JsonProperty("status") val status: ContractStatus,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: ContractStatusDetails,
        @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
        @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
        @field:JsonProperty("period") @param:JsonProperty("period") val period: Period,
        @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document>,
        @field:JsonProperty("milestones") @param:JsonProperty("milestones") val milestones: List<Milestone>,
        @field:JsonProperty("confirmationRequests") @param:JsonProperty("confirmationRequests") val confirmationRequests: List<ConfirmationRequest>,
        @field:JsonProperty("confirmationResponses") @param:JsonProperty("confirmationResponses") val confirmationResponses: List<ConfirmationResponse>,
        @field:JsonProperty("value") @param:JsonProperty("value") val value: Value
    ) {
        data class Period(
            @JsonDeserialize(using = JsonDateTimeDeserializer::class)
            @JsonSerialize(using = JsonDateTimeSerializer::class)
            @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime,

            @JsonDeserialize(using = JsonDateTimeDeserializer::class)
            @JsonSerialize(using = JsonDateTimeSerializer::class)
            @field:JsonProperty("endDate") @param:JsonProperty("endDate") val endDate: LocalDateTime
        )

        data class Document(
            @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentTypeContract,
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<String>?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("relatedConfirmations") @param:JsonProperty("relatedConfirmations") val relatedConfirmations: List<String>?
        )

        data class Milestone(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("relatedItems") @param:JsonProperty("relatedItems") val relatedItems: List<String>?,

            @field:JsonProperty("status") @param:JsonProperty("status") val status: MilestoneStatus,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("additionalInformation") @param:JsonProperty("additionalInformation") val additionalInformation: String?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @JsonDeserialize(using = JsonDateTimeDeserializer::class)
            @JsonSerialize(using = JsonDateTimeSerializer::class)
            @field:JsonProperty("dueDate") @param:JsonProperty("dueDate") val dueDate: LocalDateTime?,

            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
            @field:JsonProperty("type") @param:JsonProperty("type") val type: MilestoneType,
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @JsonDeserialize(using = JsonDateTimeDeserializer::class)
            @JsonSerialize(using = JsonDateTimeSerializer::class)
            @field:JsonProperty("dateModified") @param:JsonProperty("dateModified") val dateModified: LocalDateTime?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @JsonDeserialize(using = JsonDateTimeDeserializer::class)
            @JsonSerialize(using = JsonDateTimeSerializer::class)
            @field:JsonProperty("dateMet") @param:JsonProperty("dateMet") val dateMet: LocalDateTime?,

            @field:JsonProperty("relatedParties") @param:JsonProperty("relatedParties") val relatedParties: List<RelatedParty>
        ) {
            data class RelatedParty(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("name") @param:JsonProperty("name") val name: String
            )
        }

        data class ConfirmationRequest(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("type") @param:JsonProperty("type") val type: ConfirmationRequestType,
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
            @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: ConfirmationRequestReleaseTo,
            @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String,
            @field:JsonProperty("source") @param:JsonProperty("source") val source: ConfirmationRequestSource,
            @field:JsonProperty("requestGroups") @param:JsonProperty("requestGroups") val requestGroups: List<RequestGroup>
        ) {

            data class RequestGroup(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("requests") @param:JsonProperty("requests") val requests: List<Request>
            ) {

                data class Request(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
                    @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("relatedPerson") @param:JsonProperty("relatedPerson") val relatedPerson: RelatedPerson?
                ) {

                    data class RelatedPerson(
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                        @field:JsonProperty("name") @param:JsonProperty("name") val name: String
                    )
                }
            }
        }

        data class ConfirmationResponse(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("value") @param:JsonProperty("value") val value: Value,
            @field:JsonProperty("request") @param:JsonProperty("request") val request: String
        ) {

            data class Value(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("name") @param:JsonProperty("name") val name: String,

                @JsonDeserialize(using = JsonDateTimeDeserializer::class)
                @JsonSerialize(using = JsonDateTimeSerializer::class)
                @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("relatedPerson") @param:JsonProperty("relatedPerson") val relatedPerson: RelatedPerson?,

                @field:JsonProperty("verification") @param:JsonProperty("verification") val verifications: List<Verification>
            ) {

                data class RelatedPerson(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("name") @param:JsonProperty("name") val name: String
                )

                data class Verification(
                    @field:JsonProperty("type") @param:JsonProperty("type") val type: ConfirmationResponseType,
                    @field:JsonProperty("value") @param:JsonProperty("value") val value: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("rationale") @param:JsonProperty("rationale") val rationale: String?
                )
            }
        }

        data class Value(
            @JsonDeserialize(using = AmountDeserializer::class)
            @JsonSerialize(using = AmountSerializer::class)
            @field:JsonProperty("amount") @param:JsonProperty("amount") val amount: BigDecimal,

            @field:JsonProperty("currency") @param:JsonProperty("currency") val currency: String,

            @JsonDeserialize(using = AmountDeserializer::class)
            @JsonSerialize(using = AmountSerializer::class)
            @field:JsonProperty("amountNet") @param:JsonProperty("amountNet") val amountNet: BigDecimal,

            @field:JsonProperty("valueAddedTaxIncluded") @param:JsonProperty("valueAddedTaxIncluded") val valueAddedTaxIncluded: Boolean
        )
    }

    data class Can(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID,
        @field:JsonProperty("status") @param:JsonProperty("status") val status: CANStatus,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: CANStatusDetails
    )
}