package com.procurement.contracting.infrastructure.handler.v2.model.response


import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class GetCanByIdsResponse(
    @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>
) {
    data class Contract(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
        @param:JsonProperty("status") @field:JsonProperty("status") val status: String,
        @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: String,
        @param:JsonProperty("awardId") @field:JsonProperty("awardId") val awardId: String,
        @param:JsonProperty("lotId") @field:JsonProperty("lotId") val lotId: String,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>?,

        @param:JsonProperty("date") @field:JsonProperty("date") val date: String
    ) {
        data class Document(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: String,
            @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("relatedLots") @field:JsonProperty("relatedLots") val relatedLots: List<String>?
        )
    }
}