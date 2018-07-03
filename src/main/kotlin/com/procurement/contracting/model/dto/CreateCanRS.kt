package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.model.dto.ocds.Can

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreateCanRS(

        val cans: List<Can>?
)
