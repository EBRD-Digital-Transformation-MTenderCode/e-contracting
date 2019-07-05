package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.domain.model.document.type.DocumentTypeAmendment

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DocumentAmedment @JsonCreator constructor(

    val id: String,

    var documentType: DocumentTypeAmendment,

    var title: String?,

    var description: String?,

    var relatedLots: List<String>?
)