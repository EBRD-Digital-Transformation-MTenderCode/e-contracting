package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Document @JsonCreator constructor(

        val id: String,

        val documentType: DocumentType,

        var title: String?,

        var description: String?,

        val language: String?,

        var relatedLots: List<String>?
)