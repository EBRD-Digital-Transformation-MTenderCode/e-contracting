package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.contracting.model.dto.databinding.JsonDateDeserializer
import com.procurement.contracting.model.dto.databinding.JsonDateSerializer
import java.time.LocalDateTime
import javax.validation.Valid
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Award(

        @JsonProperty("id") @NotNull
        val id: String,

        @JsonProperty("date")
        @JsonSerialize(using = JsonDateSerializer::class)
        @JsonDeserialize(using = JsonDateDeserializer::class)
        val date: LocalDateTime?,

        @JsonProperty("description")
        val description: String?,

        @JsonProperty("status") @Valid
        val status: AwardStatus?,

        @JsonProperty("statusDetails") @Valid @NotNull
        val statusDetails: AwardStatus,

        @JsonProperty("value")
        val value: Value?,

        @JsonProperty("relatedLots")
        val relatedLots: List<String>?,

        @JsonProperty("relatedBid")
        val relatedBid: String?,

        @JsonProperty("suppliers") @Valid
        val suppliers: List<OrganizationReference>?,

        @JsonProperty("documents")
        val documents: List<Document>?
)