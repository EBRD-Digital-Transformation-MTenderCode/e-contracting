package com.procurement.contracting.infrastructure.handler.v2.model.request

import com.fasterxml.jackson.annotation.JsonProperty

data class AddSupplierReferencesInFCRequest(
    @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: String,
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: String,
    @field:JsonProperty("parties") @param:JsonProperty("parties") val parties: List<Party>
) {
    data class Party(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("name") @param:JsonProperty("name") val name: String
    )
}
