package com.procurement.contracting.infrastructure.handler.v2.model.request

import com.fasterxml.jackson.annotation.JsonProperty

data class CheckExistenceSupplierReferencesInFCRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: String
)