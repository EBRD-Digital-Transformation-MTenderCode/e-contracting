package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DocumentAward @JsonCreator constructor(

        val id: String,

        val documentType: String?,

        var title: String?,

        var description: String?
)