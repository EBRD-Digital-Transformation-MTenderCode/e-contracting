package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.model.dto.ocds.*

data class CreateContractRQ @JsonCreator constructor(

        val lots: List<Lot>,

        val items: List<Item>,

        val awards: List<Award>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreateContractRS(

        val cans: List<Can>?,

        val contracts: List<Contract>?
)