package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.can.CANId

data class GetAwardsRq @JsonCreator constructor(

    val contracts: List<ContractGetAwards>
)

data class ContractGetAwards @JsonCreator constructor(

    var id: CANId
)

data class GetAwardsRs @JsonCreator constructor(

    val cans: List<CanGetAwards>
)

data class CanGetAwards @JsonCreator constructor(

    val id: CANId,

    val awardId: AwardId
)
