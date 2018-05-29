package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonProperty

data class RelatedProcess(

        @JsonProperty("id")
        val id: String?,

        @JsonProperty("relationship")
        val relationship: List<RelatedProcessType>?,

        @JsonProperty("title")
        val title: String?,

        @JsonProperty("scheme")
        val scheme: RelatedProcessScheme?,

        @JsonProperty("identifier")
        val identifier: String?,

        @JsonProperty("uri")
        val uri: String?
)

