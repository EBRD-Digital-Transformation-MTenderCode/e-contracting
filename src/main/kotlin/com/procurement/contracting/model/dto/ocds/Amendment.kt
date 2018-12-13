package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Amendment @JsonCreator constructor(

        val rationale: String,

        val description: String?,

        val documents: List<DocumentAmendment>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DocumentAmendment @JsonCreator constructor(

        val id: String,

        var documentType: DocumentTypeContract,

        var title: String,

        var description: String?
)