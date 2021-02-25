package com.procurement.contracting.domain.model.fc

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.document.type.DocumentTypeContract
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

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("suppliers") @param:JsonProperty("suppliers") val suppliers: List<Supplier> = emptyList(),

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document> = emptyList(),

    @get:JsonProperty("isFrameworkOrDynamic") @param:JsonProperty("isFrameworkOrDynamic") val isFrameworkOrDynamic: Boolean
) {
    data class Supplier(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("name") @param:JsonProperty("name") val name: String
    )

    data class Document(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentTypeContract
    )
}
