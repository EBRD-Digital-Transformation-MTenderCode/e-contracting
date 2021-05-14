package com.procurement.contracting.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.confirmation.response.ConfirmationResponse
import java.time.LocalDateTime

data class CreateConfirmationResponseResponse(
    @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>
) {
    data class Contract(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
        @param:JsonProperty("confirmationResponses") @field:JsonProperty("confirmationResponses") val confirmationResponses: List<ConfirmationResponse>
    ) {
        data class ConfirmationResponse(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime,
            @param:JsonProperty("requestId") @field:JsonProperty("requestId") val requestId: String,
            @param:JsonProperty("type") @field:JsonProperty("type") val type: String,
            @param:JsonProperty("value") @field:JsonProperty("value") val value: String,
            @param:JsonProperty("relatedPerson") @field:JsonProperty("relatedPerson") val relatedPerson: Person,
        ) {
            companion object;

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
                    @param:JsonProperty("type") @field:JsonProperty("type") val type: String,
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
                        @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: String,
                        @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,
                    )
                }
            }
        }
    }
}

fun CreateConfirmationResponseResponse.Contract.ConfirmationResponse.Companion.fromDomain(confirmationResponse: ConfirmationResponse) =
    CreateConfirmationResponseResponse.Contract.ConfirmationResponse(
        id = confirmationResponse.id,
        date = confirmationResponse.date,
        requestId = confirmationResponse.requestId.underlying.toString(),
        type = confirmationResponse.type.key,
        value = confirmationResponse.value,
        relatedPerson = confirmationResponse.relatedPerson.convert()
    )

private fun ConfirmationResponse.Person.convert() =
    CreateConfirmationResponseResponse.Contract.ConfirmationResponse.Person(
        id = id,
        title = title,
        name = name,
        identifier = identifier.convert(),
        businessFunctions = businessFunctions
            .map { businessFunction -> businessFunction.convert() }
    )

private fun ConfirmationResponse.Person.Identifier.convert() =
    CreateConfirmationResponseResponse.Contract.ConfirmationResponse.Person.Identifier(
        id = id,
        scheme = scheme,
        uri = uri
    )

private fun ConfirmationResponse.Person.BusinessFunction.convert() =
    CreateConfirmationResponseResponse.Contract.ConfirmationResponse.Person.BusinessFunction(
        id = id,
        type = type.key,
        jobTitle = jobTitle,
        period = period.convert(),
        documents = documents.orEmpty()
            .map { document -> document.convert() }
    )

private fun ConfirmationResponse.Person.BusinessFunction.Period.convert() =
    CreateConfirmationResponseResponse.Contract.ConfirmationResponse.Person.BusinessFunction.Period(
        startDate = startDate
    )

private fun ConfirmationResponse.Person.BusinessFunction.Document.convert() =
    CreateConfirmationResponseResponse.Contract.ConfirmationResponse.Person.BusinessFunction.Document(
        id = id,
        documentType = documentType.key,
        title = title,
        description = description
    )