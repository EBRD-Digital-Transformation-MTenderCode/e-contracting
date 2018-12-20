package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.model.dto.ocds.DocumentAmedment
import com.procurement.contracting.model.dto.ocds.DocumentContract
import com.procurement.contracting.model.dto.ocds.DocumentTypeAmendment
import com.procurement.contracting.model.dto.ocds.DocumentTypeContract

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UpdateDocumentsRq @JsonCreator constructor(

        val documents: List<DocumentAmedment>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UpdateDocumentsRs @JsonCreator constructor(

        val contract: UpdateDocumentContract
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UpdateDocumentContract @JsonCreator constructor(

        val id: String,
        val documents: List<DocumentAmedment>
)