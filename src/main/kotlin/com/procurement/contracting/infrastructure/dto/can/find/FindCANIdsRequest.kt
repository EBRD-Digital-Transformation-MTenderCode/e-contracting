package com.procurement.contracting.infrastructure.dto.can.find

import com.fasterxml.jackson.annotation.JsonProperty

data class FindCANIdsRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: String,
    @param:JsonProperty("states") @field:JsonProperty("states") val states: List<State>?,
    @param:JsonProperty("lotIds") @field:JsonProperty("lotIds") val lotIds: List<String>?
) {
    data class State(
        @param:JsonProperty("status") @field:JsonProperty("status") val status: String?,
        @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: String?
    )
}