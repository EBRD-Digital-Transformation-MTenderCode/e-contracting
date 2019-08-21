package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.domain.model.document.type.DocumentTypeUpdateCan
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.model.dto.ocds.DocumentContract

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UpdateDocumentsRq @JsonCreator constructor(

    val documents: List<DocumentUpdate>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DocumentUpdate @JsonCreator constructor(

    val id: String,

    var documentType: DocumentTypeUpdateCan,

    var title: String?,

    var description: String?,

    var relatedLots: List<LotId>?,

    var relatedConfirmations: List<String>? = null
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UpdateDocumentsRs @JsonCreator constructor(

    val contract: UpdateDocumentContract
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UpdateDocumentContract @JsonCreator constructor(

    val id: String,
    val documents: List<DocumentContract>
)