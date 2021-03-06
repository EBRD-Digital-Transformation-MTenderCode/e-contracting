package com.procurement.contracting.infrastructure.handler.v1.model.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.domain.model.ac.id.AwardContractId
import com.procurement.contracting.domain.model.ac.status.AwardContractStatus
import com.procurement.contracting.domain.model.ac.status.AwardContractStatusDetails
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.model.dto.ocds.Milestone

data class ActivationAcRs @JsonCreator constructor(

    val relatedLots: List<LotId>,

    val contract: ActivationContract,

    val cans: List<ActivationCan>
)


@JsonInclude(JsonInclude.Include.NON_NULL)
data class ActivationContract @JsonCreator constructor(

    val id: AwardContractId,

    var status: AwardContractStatus,

    var statusDetails: AwardContractStatusDetails,

    val milestones: MutableList<Milestone>?
)

data class ActivationCan @JsonCreator constructor(

    val id: CANId,

    var status: CANStatus,

    var statusDetails: CANStatusDetails
)
