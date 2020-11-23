package com.procurement.contracting.domain.model.fc

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.fc.id.FrameworkContractId
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatus
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatusDetails
import java.time.LocalDateTime

data class FrameworkContract(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: FrameworkContractId,
    @field:JsonProperty("token") @param:JsonProperty("token") val token: Token,
    @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: Owner,
    @field:JsonProperty("status") @param:JsonProperty("status") val status: FrameworkContractStatus,
    @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: FrameworkContractStatusDetails,
    @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,
    @get:JsonProperty("isFrameworkOrDynamic") @param:JsonProperty("isFrameworkOrDynamic") val isFrameworkOrDynamic: Boolean
)
