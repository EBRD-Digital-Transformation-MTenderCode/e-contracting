package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.model.dto.ocds.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ContractProcess @JsonCreator constructor(

        val awards: Award,

        val contracts: Contract,

        var planning: Planning?,

        var buyer: OrganizationReferenceBuyer?,

        var payer: OrganizationReference?,

        var funder: OrganizationReference?,

        var treasuryBudgetSources: List<TreasuryBudgetSource>?

)