package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Lot(

        @JsonProperty("id") @NotNull
        val id: String,

        @JsonProperty("title") @NotNull
        val title: String,

        @JsonProperty("description") @NotNull
        val description: String,

        @JsonProperty("status")
        val status: TenderStatus?,

        @JsonProperty("statusDetails")
        val statusDetails: TenderStatusDetails?
)