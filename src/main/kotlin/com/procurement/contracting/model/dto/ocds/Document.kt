package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Document(

        @JsonProperty("id") @NotNull
        val id: String,

        @JsonProperty("documentType")
        val documentType: DocumentType?,

        @JsonProperty("title")
        val title: String?,

        @JsonProperty("description")
        val description: String?,

        @JsonProperty("language")
        val language: String?,

        @JsonProperty("relatedLots")
        val relatedLots: List<String>?
)