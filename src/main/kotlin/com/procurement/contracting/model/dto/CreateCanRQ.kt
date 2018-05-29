package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.model.dto.ocds.Award
import javax.validation.Valid
import javax.validation.constraints.NotEmpty

data class CreateCanRQ(

        @JsonProperty("awards") @Valid @NotEmpty
        val awards: List<Award>
)
