package com.procurement.contracting.domain.model.confirmation.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.Token

data class ConfirmationRequest(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: ConfirmationRequestId,
    @field:JsonProperty("type") @param:JsonProperty("type") val type: ConfirmationRequestType,
    @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: ConfirmationRequestReleaseTo,
    @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String,
    @field:JsonProperty("source") @param:JsonProperty("source") val source: ConfirmationRequestSource,
    @field:JsonProperty("requestGroups") @param:JsonProperty("requestGroups") val requestGroups: List<RequestGroup>,
) {
    data class RequestGroup(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: String,
        @field:JsonProperty("token") @param:JsonProperty("token") val token: Token,
        @field:JsonProperty("relatedOrganization") @param:JsonProperty("relatedOrganization") val relatedOrganization: Organization
    ) {
        data class Organization(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
        )
    }
}
