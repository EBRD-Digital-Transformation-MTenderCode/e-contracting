package com.procurement.contracting.infrastructure.dto.can.cancel

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.model.dto.ocds.ContractStatus
import com.procurement.contracting.model.dto.ocds.ContractStatusDetails
import com.procurement.contracting.model.dto.ocds.DocumentTypeAmendment

data class CancelCANResponse(
    @field:JsonProperty("cans") @param:JsonProperty("cans") val cans: List<CAN>,
    @field:JsonProperty("acCancel") @param:JsonProperty("acCancel") val acCancel: Boolean,
    @field:JsonProperty("lotId") @param:JsonProperty("lotId") val lotId: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("contract") @param:JsonProperty("contract") val contract: Contract?
) {

    data class CAN(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("status") @param:JsonProperty("status") val status: ContractStatus,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: ContractStatusDetails,

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
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentTypeAmendment,
                @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String?
            )
        }
    }

    data class Contract(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("status") @param:JsonProperty("status") val status: ContractStatus,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: ContractStatusDetails
    )
}