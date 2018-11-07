package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Lot @JsonCreator constructor(

        val id: String,

        val title: String,

        val description: String,

        val status: TenderStatus?,

        val statusDetails: TenderStatusDetails?
)