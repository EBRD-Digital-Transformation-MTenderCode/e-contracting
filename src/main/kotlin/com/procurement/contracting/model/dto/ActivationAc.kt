package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.model.dto.ocds.ContractStatus
import com.procurement.contracting.model.dto.ocds.ContractStatusDetails
import com.procurement.contracting.model.dto.ocds.Milestone

data class ActivationAcRs @JsonCreator constructor(

        val relatedLots: List<String>,

        val contract: ActivationContract,

        val cans: List<ActivationCan>
)


@JsonInclude(JsonInclude.Include.NON_NULL)
data class ActivationContract @JsonCreator constructor(

        var status: ContractStatus,

        var statusDetails: ContractStatusDetails,

        val milestones: HashSet<Milestone>?
)

data class ActivationCan @JsonCreator constructor(

        val id: String,

        var status: ContractStatus,

        var statusDetails: ContractStatusDetails
)