package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.model.dto.ocds.*
import javax.validation.Valid
import javax.validation.constraints.NotEmpty

data class CreateContractRQ @JsonCreator constructor(

        @field:Valid @field:NotEmpty
        val lots: List<Lot>,

        @field:Valid @field:NotEmpty
        val items: List<Item>,

        @field:Valid @field:NotEmpty
        val awards: List<Award>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreateContractRS(

        val cans: List<Can>?,

        val contracts: List<Contract>?
)