package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.model.dto.ocds.Award
import com.procurement.contracting.model.dto.ocds.Item
import com.procurement.contracting.model.dto.ocds.Lot
import javax.validation.Valid
import javax.validation.constraints.NotEmpty

data class CreateContractRQ(

        @JsonProperty("lots") @Valid @NotEmpty
        val lots: List<Lot>,

        @JsonProperty("items") @Valid @NotEmpty
        val items: List<Item>,

        @JsonProperty("awards") @Valid @NotEmpty
        val awards: List<Award>
)
