package com.procurement.contracting.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonProperty

data class FindContractDocumentIdResponse(
    @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>
) {
    data class Contract(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
        @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>
    ) {
        data class Document(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String
        )
    }
}