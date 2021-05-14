package com.procurement.contracting.infrastructure.handler.v2.model.request

import com.fasterxml.jackson.annotation.JsonProperty

data class AddGeneratedDocumentToContractRequest(
    @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: String,
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: String,
    @field:JsonProperty("processInitiator") @param:JsonProperty("processInitiator") val processInitiator: String,
    @field:JsonProperty("contracts") @param:JsonProperty("contracts") val contracts: List<Contract>
) {
    data class Contract(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document>
    ) {
        data class Document(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        )
    }
}
