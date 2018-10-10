package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime
import javax.validation.Valid
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Award @JsonCreator constructor(

        val id: String,

        val date: LocalDateTime?,

        val description: String?,

        val status: AwardStatus?,

        val statusDetails: AwardStatus,

        val value: Value?,

        val relatedLots: List<String>?,

        val relatedBid: String?,

        val suppliers: List<OrganizationReference>?,

        val documents: List<Document>?
)