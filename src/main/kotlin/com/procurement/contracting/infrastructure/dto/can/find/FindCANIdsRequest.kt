package com.procurement.contracting.infrastructure.dto.can.find

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class FindCANIdsRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: String,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @param:JsonProperty("states") @field:JsonProperty("states") val states: List<State>?,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @param:JsonProperty("lotIds") @field:JsonProperty("lotIds") val lotIds: List<String>?
) {
    data class State(
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("status") @field:JsonProperty("status") val status: String?,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: String?
    )
}