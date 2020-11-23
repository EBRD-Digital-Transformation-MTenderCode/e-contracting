package com.procurement.contracting.infrastructure.handler.v1.model.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.model.dto.ocds.PlanningBudgetSource

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GetActualBsRs(

        val language: String?,

        val actualBudgetSource: Set<PlanningBudgetSource>?,

        val itemsCPVs: Set<String>
)