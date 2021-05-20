package com.procurement.contracting.infrastructure.handler.v2.model.response


import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.pac.PacId

data class GetSupplierIdsByContractResponse(
    @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>
) {
    data class Contract(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: PacId,
        @param:JsonProperty("suppliers") @field:JsonProperty("suppliers") val suppliers: List<Supplier>
    ) {
        data class Supplier(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String
        )
    }
}