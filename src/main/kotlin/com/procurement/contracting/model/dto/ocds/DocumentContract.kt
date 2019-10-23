package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.domain.model.document.type.DocumentTypeContract
import com.procurement.contracting.domain.model.lot.LotId

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DocumentContract @JsonCreator constructor(

    val id: String,

    var documentType: DocumentTypeContract,

    var title: String?,

    var description: String?,

    var relatedLots: List<LotId>?,

    var relatedConfirmations: List<String>? = null
)
