package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.contracting.model.dto.ocds.Award
import com.procurement.contracting.model.dto.ocds.Item
import com.procurement.contracting.model.dto.ocds.Lot
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
