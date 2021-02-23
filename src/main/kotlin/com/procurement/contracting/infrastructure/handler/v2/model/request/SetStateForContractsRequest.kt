package com.procurement.contracting.infrastructure.handler.v2.model.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class SetStateForContractsRequest(
    @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: String,
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: String,
    @field:JsonProperty("pmd") @param:JsonProperty("pmd") val pmd: String,
    @field:JsonProperty("country") @param:JsonProperty("country") val country: String,
    @field:JsonProperty("operationType") @param:JsonProperty("operationType") val operationType: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender?,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("contracts") @param:JsonProperty("contracts") val contracts: List<Contract>?
) {
    data class Tender(
        @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>
    ) {
        data class Lot(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String
        )
    }

    data class Contract(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String
    )
}
