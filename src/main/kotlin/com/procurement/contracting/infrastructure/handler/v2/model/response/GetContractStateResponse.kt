package com.procurement.contracting.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.fc.FrameworkContract
import com.procurement.contracting.model.dto.ocds.AwardContract

data class GetContractStateResponse(
    @field:JsonProperty("contracts") @param:JsonProperty("contracts") val contracts: List<Contract>,
) {
    data class Contract(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("status") @param:JsonProperty("status") val status: String,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String,
    ) { companion object; }
}

fun GetContractStateResponse.Contract.Companion.fromDomain(contract: FrameworkContract) =
    GetContractStateResponse.Contract(
        id = contract.id.underlying,
        status = contract.status.key,
        statusDetails = contract.statusDetails.key
    )

fun GetContractStateResponse.Contract.Companion.fromDomain(contract: AwardContract) =
    GetContractStateResponse.Contract(
        id = contract.id.underlying,
        status = contract.status.key,
        statusDetails = contract.statusDetails.key
    )
