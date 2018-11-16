package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.model.dto.ocds.*
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ContractProcess @JsonCreator constructor(

        val awards: HashSet<Award>,

        val contracts: HashSet<Contract>,

        var planning: Planning?,

        var buyer: OrganizationReferenceBuyer?,

        var funders: HashSet<OrganizationReference>?,

        var payers: HashSet<OrganizationReference>?,

        var treasuryBudgetSources: List<TreasuryBudgetSource>?

)