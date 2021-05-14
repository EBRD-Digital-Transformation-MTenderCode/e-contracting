package com.procurement.contracting.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequest

data class GetRequestByConfirmationResponseResponse(
    @field:JsonProperty("contracts") @param:JsonProperty("contracts") val contracts: List<Contract>,
) {
    data class Contract(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("confirmationRequests") @param:JsonProperty("confirmationRequests") val confirmationRequests: List<ConfirmationRequest>,
    ) {
        data class ConfirmationRequest(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("type") @param:JsonProperty("type") val type: String,
            @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: String,
            @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String,
            @field:JsonProperty("source") @param:JsonProperty("source") val source: String,
            @field:JsonProperty("requests") @param:JsonProperty("requests") val requests: List<Request>,
        ) { companion object;

            data class Request(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: String,
                @field:JsonProperty("token") @param:JsonProperty("token") val token: String,
                @field:JsonProperty("relatedOrganization") @param:JsonProperty("relatedOrganization") val relatedOrganization: Organization
            ) {
                data class Organization(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
                )
            }
        }
    }
}

fun GetRequestByConfirmationResponseResponse.Contract.ConfirmationRequest.Companion.fromDomain(confirmationRequest: ConfirmationRequest) =
    GetRequestByConfirmationResponseResponse.Contract.ConfirmationRequest(
        id = confirmationRequest.id.underlying.toString(),
        type = confirmationRequest.type.key,
        relatesTo = confirmationRequest.relatesTo.key,
        relatedItem = confirmationRequest.relatedItem,
        source = confirmationRequest.source.key,
        requests = confirmationRequest.requests
            .map { it.convert() }
    )

private fun ConfirmationRequest.Request.convert() =
    GetRequestByConfirmationResponseResponse.Contract.ConfirmationRequest.Request(
        id = id,
        owner = owner,
        token = token.underlying.toString(),
        relatedOrganization = relatedOrganization.convert()
    )

private fun ConfirmationRequest.Request.Organization.convert() =
    GetRequestByConfirmationResponseResponse.Contract.ConfirmationRequest.Request.Organization(
        id = id,
        name = name
    )
