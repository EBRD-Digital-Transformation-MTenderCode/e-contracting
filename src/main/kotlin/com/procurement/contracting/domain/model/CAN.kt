package com.procurement.contracting.domain.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.contracting.infrastructure.bind.JsonDateTimeDeserializer
import com.procurement.contracting.infrastructure.bind.JsonDateTimeSerializer
import com.procurement.contracting.model.dto.ocds.ContractStatus
import com.procurement.contracting.model.dto.ocds.ContractStatusDetails
import com.procurement.contracting.model.dto.ocds.DocumentTypeAmendment
import com.procurement.contracting.model.dto.ocds.DocumentTypeContract
import java.time.LocalDateTime
import java.util.*

data class CAN(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID,
    @field:JsonProperty("token") @param:JsonProperty("token") val token: UUID,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("awardId") @param:JsonProperty("awardId") val awardId: String?,
    @field:JsonProperty("lotId") @param:JsonProperty("lotId") val lotId: String,

    @JsonDeserialize(using = JsonDateTimeDeserializer::class)
    @JsonSerialize(using = JsonDateTimeSerializer::class)
    @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,

    @field:JsonProperty("status") @param:JsonProperty("status") val status: ContractStatus,
    @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: ContractStatusDetails,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document>?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("amendment") @param:JsonProperty("amendment") val amendment: Amendment?
) {

    data class Amendment(
        @field:JsonProperty("rationale") @param:JsonProperty("rationale") val rationale: String,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document>?
    ) {

        data class Document(
            @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentTypeAmendment,
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?
        )
    }

    data class Document(
        @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentTypeContract,
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<String>?
    )
}
