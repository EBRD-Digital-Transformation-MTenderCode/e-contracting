package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Person @JsonCreator constructor(

        var title: String,

        var name: String,

        val identifier: Identifier,

        var businessFunctions: List<BusinessFunction>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BusinessFunction @JsonCreator constructor(

        val id: String,

        var type: String,

        var jobTitle: String,

        var period: Period,

        var documents: List<DocumentBF>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DocumentBF @JsonCreator constructor(

        val id: String,

        val documentType: DocumentTypeBF,

        var title: String?,

        var description: String?
)