package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import javax.validation.constraints.NotNull

data class Document @JsonCreator constructor(

        @field:NotNull
        val id: String,

        val documentType: DocumentType?,

        val title: String?,

        val description: String?,

        val language: String?,

        val relatedLots: List<String>?
)