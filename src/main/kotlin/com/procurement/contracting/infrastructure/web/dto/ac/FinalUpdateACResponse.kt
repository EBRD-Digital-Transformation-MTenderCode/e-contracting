package com.procurement.contracting.infrastructure.web.dto.ac


import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestSource
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestType
import com.procurement.contracting.domain.model.contract.status.ContractStatus
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails
import com.procurement.contracting.domain.model.document.type.DocumentTypeContract
import com.procurement.contracting.domain.model.milestone.status.MilestoneStatus
import com.procurement.contracting.domain.model.milestone.type.MilestoneType
import com.procurement.contracting.infrastructure.amount.AmountDeserializer
import com.procurement.contracting.infrastructure.amount.AmountSerializer
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class FinalUpdateACResponse(
    @field:JsonProperty("contracts") @param:JsonProperty("contracts") val contracts: Contracts,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("approveBody") @param:JsonProperty("approveBody") val approveBody: ApproveBody?
) {
    data class Contracts(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,
        @field:JsonProperty("awardID") @param:JsonProperty("awardID") val awardID: UUID,
        @field:JsonProperty("status") @param:JsonProperty("status") val status: ContractStatus,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: ContractStatusDetails,
        @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
        @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
        @field:JsonProperty("period") @param:JsonProperty("period") val period: Period,
        @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document>,
        @field:JsonProperty("milestones") @param:JsonProperty("milestones") val milestones: List<Milestone>,
        @field:JsonProperty("confirmationRequests") @param:JsonProperty("confirmationRequests") val confirmationRequests: List<ConfirmationRequest>,
        @field:JsonProperty("value") @param:JsonProperty("value") val value: Value
    ) {
        data class Document(
            @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentTypeContract,
            @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<UUID>?
        )

        data class Value(

            @JsonDeserialize(using = AmountDeserializer::class)
            @JsonSerialize(using = AmountSerializer::class)
            @field:JsonProperty("amount") @param:JsonProperty("amount") val amount: BigDecimal,

            @field:JsonProperty("currency") @param:JsonProperty("currency") val currency: String,

            @JsonDeserialize(using = AmountDeserializer::class)
            @JsonSerialize(using = AmountSerializer::class)
            @field:JsonProperty("amountNet") @param:JsonProperty("amountNet") val amountNet: BigDecimal,

            @field:JsonProperty("valueAddedTaxincluded") @param:JsonProperty("valueAddedTaxincluded") val valueAddedTaxincluded: Boolean
        )

        data class Period(
            @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime,
            @field:JsonProperty("endDate") @param:JsonProperty("endDate") val endDate: LocalDateTime
        )

        data class Milestone(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("relatedItems") @param:JsonProperty("relatedItems") val relatedItems: List<UUID>?,

            @field:JsonProperty("status") @param:JsonProperty("status") val status: MilestoneStatus,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("additionalInformation") @param:JsonProperty("additionalInformation") val additionalInformation: String?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("dueDate") @param:JsonProperty("dueDate") val dueDate: LocalDateTime?,

            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
            @field:JsonProperty("type") @param:JsonProperty("type") val type: MilestoneType,
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
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
            @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: String,
            @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: UUID,
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

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @field:JsonProperty("relatedPerson") @param:JsonProperty("relatedPerson") val relatedPerson: RelatedPerson?
                ) {
                    data class RelatedPerson(
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                        @field:JsonProperty("name") @param:JsonProperty("name") val name: String
                    )
                }
            }
        }
    }

    data class ApproveBody(
        @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: Identifier,
        @field:JsonProperty("contactPoint") @param:JsonProperty("contactPoint") val contactPoint: ContactPoint
    ) {
        data class Identifier(
            @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("legalName") @param:JsonProperty("legalName") val legalName: String,
            @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String
        )

        data class ContactPoint(
            @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
            @field:JsonProperty("email") @param:JsonProperty("email") val email: String,
            @field:JsonProperty("telephone") @param:JsonProperty("telephone") val telephone: String,
            @field:JsonProperty("faxNumber") @param:JsonProperty("faxNumber") val faxNumber: String,
            @field:JsonProperty("url") @param:JsonProperty("url") val url: String
        )
    }
}