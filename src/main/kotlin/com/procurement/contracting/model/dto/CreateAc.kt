package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.model.dto.ocds.Award
import com.procurement.contracting.model.dto.ocds.Can
import com.procurement.contracting.model.dto.ocds.Contract

data class CreateAcRq @JsonCreator constructor(

        val awards: List<Award>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreateAcRs(

        val cans: List<Can>?,

        val contracts: List<Contract>?
)