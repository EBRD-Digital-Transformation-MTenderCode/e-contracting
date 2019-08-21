package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.contract.status.ContractStatus
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.model.dto.ocds.Milestone

data class ActivationAcRs @JsonCreator constructor(

        val relatedLots: List<LotId>,

        val contract: ActivationContract,

        val cans: List<ActivationCan>
)


@JsonInclude(JsonInclude.Include.NON_NULL)
data class ActivationContract @JsonCreator constructor(

    var status: ContractStatus,

    var statusDetails: ContractStatusDetails,

    val milestones: MutableList<Milestone>?
)

data class ActivationCan @JsonCreator constructor(

    val id: String,

    var status: CANStatus,

    var statusDetails: CANStatusDetails
)