package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.model.dto.ocds.Contract

@JsonInclude(JsonInclude.Include.NON_NULL)
data class FinalUpdateAcRq @JsonCreator constructor(
        val documents: List<Document>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Document @JsonCreator constructor(
        val id: String
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class FinalUpdateAcRs @JsonCreator constructor(

        val contract: Contract

)


