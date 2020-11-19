package com.procurement.contracting.domain.model.can

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.document.type.DocumentTypeAmendment
import com.procurement.contracting.domain.model.document.type.DocumentTypeContract
import com.procurement.contracting.domain.model.lot.LotId
import java.time.LocalDateTime
import java.util.*

data class CAN(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: CANId,
    @field:JsonProperty("token") @param:JsonProperty("token") val token: UUID,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("awardId") @param:JsonProperty("awardId") val awardId: AwardId?,
    @field:JsonProperty("lotId") @param:JsonProperty("lotId") val lotId: LotId,
    @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,

    @field:JsonProperty("status") @param:JsonProperty("status") val status: CANStatus,
    @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: CANStatusDetails,

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
        @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<LotId>?
    )
}
