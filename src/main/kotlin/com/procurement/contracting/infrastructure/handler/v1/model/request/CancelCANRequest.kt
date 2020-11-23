package com.procurement.contracting.infrastructure.handler.v1.model.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.document.type.DocumentTypeAmendment

data class CancelCANRequest(
    @field:JsonProperty("contract") @param:JsonProperty("contract") val contract: Contract
) {

    data class Contract(
        @field:JsonProperty("amendment") @param:JsonProperty("amendment") val amendment: Amendment
    ) {

        data class Amendment(
            @field:JsonProperty("rationale") @param:JsonProperty("rationale") val rationale: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document>?
        ) {

            data class Document(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentTypeAmendment,
                @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String?
            )
        }
    }
}