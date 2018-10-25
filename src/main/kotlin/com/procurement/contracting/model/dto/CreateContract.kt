package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.model.dto.ocds.Award
import com.procurement.contracting.model.dto.ocds.Can
import com.procurement.contracting.model.dto.ocds.Contract

data class CreateContractRQ @JsonCreator constructor(

        val awards: List<Award>,

        val tender: CreateContractTender
)

data class CreateContractTender @JsonCreator constructor(

        val mainProcurementCategory: String
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreateContractRS(

        val cans: List<Can>?,

        val contracts: List<Contract>?
)