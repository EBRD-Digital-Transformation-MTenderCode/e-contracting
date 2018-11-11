package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Person @JsonCreator constructor(

        val title: String,

        val name: String,

        val identifier: Identifier,

        val businessFunctions: List<BusinessFunction>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BusinessFunction @JsonCreator constructor(

        val type: String,

        val jobTitle: String,

        val period: Period,

        val documents: List<Document>
)