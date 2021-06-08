package com.procurement.contracting.infrastructure.handler.v2.model.response


import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.document.type.DocumentTypeContract
import com.procurement.contracting.domain.model.lot.LotId
import java.time.LocalDateTime

data class GetCanByIdsResponse(
    @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>
) {
    data class Contract(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: CANId,
        @param:JsonProperty("status") @field:JsonProperty("status") val status: CANStatus,
        @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: CANStatusDetails,
        @param:JsonProperty("awardId") @field:JsonProperty("awardId") val awardId: AwardId,
        @param:JsonProperty("lotId") @field:JsonProperty("lotId") val lotId: LotId,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>?,

        @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime
    ) {
        data class Document(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: DocumentTypeContract,
            @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("relatedLots") @field:JsonProperty("relatedLots") val relatedLots: List<LotId>?
        )
    }
}