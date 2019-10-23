package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.domain.model.document.type.DocumentTypeAward
import com.procurement.contracting.domain.model.lot.LotId

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DocumentAward @JsonCreator constructor(

    val id: String,

    var documentType: DocumentTypeAward,

    var title: String?,

    var description: String?,

    var relatedLots: List<LotId>?
)
