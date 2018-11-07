package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.model.dto.ocds.Award
import com.procurement.contracting.model.dto.ocds.Contract
import com.procurement.contracting.model.dto.ocds.OrganizationReference
import com.procurement.contracting.model.dto.ocds.Planning

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ContractProcess @JsonCreator constructor(

        val planning: Planning?,

        val contracts: Contract,

        val awards: Award,

        val buyer: OrganizationReference?
)