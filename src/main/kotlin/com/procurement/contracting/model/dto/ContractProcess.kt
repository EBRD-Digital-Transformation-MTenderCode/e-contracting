package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.model.dto.ocds.*
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ContractProcess @JsonCreator constructor(

        var planning: Planning? = null,

        val contract: Contract,

        val award: Award,

        var buyer: OrganizationReferenceBuyer? = null,

        var funders: HashSet<OrganizationReference>? = null,

        var payers: HashSet<OrganizationReference>? = null,

        var treasuryBudgetSources: List<TreasuryBudgetSource>? = null,

        var treasuryData: TreasuryData? = null

)