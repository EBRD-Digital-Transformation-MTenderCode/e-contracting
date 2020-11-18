package com.procurement.contracting.application.repository.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.infrastructure.handler.v1.model.request.TreasuryBudgetSource
import com.procurement.contracting.model.dto.ocds.Contract
import com.procurement.contracting.model.dto.ocds.ContractedAward
import com.procurement.contracting.model.dto.ocds.OrganizationReference
import com.procurement.contracting.model.dto.ocds.OrganizationReferenceBuyer
import com.procurement.contracting.model.dto.ocds.Planning
import com.procurement.contracting.model.dto.ocds.TreasuryData
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ContractProcess @JsonCreator constructor(

        var planning: Planning? = null,

        val contract: Contract,

        val award: ContractedAward,

        var buyer: OrganizationReferenceBuyer? = null,

        var funders: HashSet<OrganizationReference>? = null,

        var payers: HashSet<OrganizationReference>? = null,

        var treasuryBudgetSources: List<TreasuryBudgetSource>? = null,

        var treasuryData: TreasuryData? = null

)