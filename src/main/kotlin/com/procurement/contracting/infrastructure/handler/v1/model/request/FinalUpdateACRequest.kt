package com.procurement.contracting.infrastructure.handler.v1.model.request


import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class FinalUpdateACRequest(
    @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document>
) {
    data class Document(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID
    )
}