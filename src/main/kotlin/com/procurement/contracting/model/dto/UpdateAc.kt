package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.model.dto.ocds.*

data class UpdateAcRq @JsonCreator constructor(


        val planning: Planning?,

        val contracts: Contract,

        val awards: Award,

        val buyer: OrganizationReference?,

        val treasuryBudgetSources: List<PlanningBudgetSource>?

)