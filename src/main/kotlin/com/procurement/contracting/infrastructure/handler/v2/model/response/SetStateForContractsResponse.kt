package com.procurement.contracting.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.can.CAN
import com.procurement.contracting.domain.model.fc.FrameworkContract
import com.procurement.contracting.domain.model.fc.Pac

data class SetStateForContractsResponse(
    @field:JsonProperty("contracts") @param:JsonProperty("contracts") val contracts: List<Contract>,
) {
    data class Contract(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("status") @param:JsonProperty("status") val status: String,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String?
    )

    companion object {
        fun fromDomain(pac: Pac): Contract =
            Contract(
                id = pac.id.underlying,
                status = pac.status.key,
                statusDetails = pac.statusDetails?.key
            )

        fun fromDomain(fc: FrameworkContract): Contract =
            Contract(
                id = fc.id.underlying,
                status = fc.status.key,
                statusDetails = fc.statusDetails.key
            )

        fun fromDomain(can: CAN): Contract =
            Contract(
                id = can.id.underlying.toString(),
                status = can.status.key,
                statusDetails = can.statusDetails.key
            )
    }
}
