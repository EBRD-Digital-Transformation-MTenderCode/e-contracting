package com.procurement.contracting.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.can.CAN
import com.procurement.contracting.domain.model.fc.FrameworkContract
import com.procurement.contracting.domain.model.fc.PacEntity
import com.procurement.contracting.model.dto.ocds.AwardContract

data class GetContractStateResponse(
    @field:JsonProperty("contracts") @param:JsonProperty("contracts") val contracts: List<Contract>,
) {
    data class Contract(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("status") @param:JsonProperty("status") val status: String,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String?,
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

fun GetContractStateResponse.Contract.Companion.fromDomain(contract: CAN) =
    GetContractStateResponse.Contract(
        id = contract.id.underlying.toString(),
        status = contract.status.key,
        statusDetails = contract.statusDetails.key
    )

fun GetContractStateResponse.Contract.Companion.fromDomain(contract: PacEntity) =
    GetContractStateResponse.Contract(
        id = contract.id,
        status = contract.status,
        statusDetails = contract.statusDetails
    )
