package com.procurement.contracting.domain.model.confirmation.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.bid.BusinessFunctionType
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestId
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationResponseId
import com.procurement.contracting.domain.model.document.type.DocumentTypeBF
import java.time.LocalDateTime

data class ConfirmationResponse(
    @param:JsonProperty("id") @field:JsonProperty("id") val id: ConfirmationResponseId,
    @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime,
    @param:JsonProperty("requestId") @field:JsonProperty("requestId") val requestId: ConfirmationRequestId,
    @param:JsonProperty("type") @field:JsonProperty("type") val type: ConfirmationResponseType,
    @param:JsonProperty("value") @field:JsonProperty("value") val value: String,
    @param:JsonProperty("relatedPerson") @field:JsonProperty("relatedPerson") val relatedPerson: Person,
) {
    data class Person(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
        @param:JsonProperty("title") @field:JsonProperty("title") val title: String,
        @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
        @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier,
        @param:JsonProperty("businessFunctions") @field:JsonProperty("businessFunctions") val businessFunctions: List<BusinessFunction>,
    ) {
        data class Identifier(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?,
        )

        data class BusinessFunction(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("type") @field:JsonProperty("type") val type: BusinessFunctionType,
            @param:JsonProperty("jobTitle") @field:JsonProperty("jobTitle") val jobTitle: String,
            @param:JsonProperty("period") @field:JsonProperty("period") val period: Period,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>?,
        ) {
            data class Period(
                @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime
            )

            data class Document(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: DocumentTypeBF,
                @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,
            )
        }
    }
}