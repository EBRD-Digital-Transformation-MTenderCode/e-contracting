package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.model.dto.ocds.Can
import com.procurement.contracting.model.dto.ocds.Contract

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreateContractRS(

        val cans: List<Can>?,

        val contracts: List<Contract>?
)
