package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.model.dto.ocds.Contract
import javax.validation.Valid
import javax.validation.constraints.NotNull

data class Can(

        @JsonProperty("token") @NotNull
        val token: String,

        @JsonProperty("contract") @Valid @NotNull
        val contract: Contract
)
