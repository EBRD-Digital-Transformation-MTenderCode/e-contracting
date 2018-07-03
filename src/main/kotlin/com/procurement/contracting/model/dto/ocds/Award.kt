package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime
import javax.validation.Valid
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Award @JsonCreator constructor(

        @field:NotNull
        val id: String,

        val date: LocalDateTime?,

        val description: String?,

        val status: AwardStatus?,

        @field:NotNull
        val statusDetails: AwardStatus,

        val value: Value?,

        val relatedLots: List<String>?,

        val relatedBid: String?,

        @field:Valid
        val suppliers: List<OrganizationReference>?,

        @field:Valid
        val documents: List<Document>?
)