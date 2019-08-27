package com.procurement.contracting.infrastructure.web.dto.ac


import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class FinalUpdateACRequest(
    @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document>
) {
    data class Document(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID
    )
}