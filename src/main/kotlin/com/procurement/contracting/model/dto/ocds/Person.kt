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

        val type: String,

        val jobTitle: String,

        val period: Period,

        val documents: List<DocumentBF>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DocumentBF @JsonCreator constructor(

        val id: String,

        val documentType: DocumentTypeBF,

        var title: String?,

        var description: String?
)