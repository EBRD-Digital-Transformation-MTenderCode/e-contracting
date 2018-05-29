package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.model.dto.ocds.Contract
import javax.validation.Valid
import javax.validation.constraints.NotEmpty

data class CreateContractRS(

        @JsonProperty("cans") @Valid @NotEmpty
        val cans: List<Can>?,

        @JsonProperty("contracts") @Valid @NotEmpty
        val contracts: List<Contract>?
)
