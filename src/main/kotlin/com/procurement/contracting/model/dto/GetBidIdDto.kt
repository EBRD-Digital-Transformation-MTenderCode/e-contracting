package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.model.dto.ocds.PlanningBudgetSource

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GetBidIdRs(

        val relatedBid: String
)