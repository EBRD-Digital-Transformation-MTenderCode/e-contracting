package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator


data class GetAwardsRq(

        val contracts: List<ContractGetAwards>
)

data class ContractGetAwards @JsonCreator constructor(

        var id: String
)

data class GetAwardsRs(

        val cans: List<CanGetAwards>
)

data class CanGetAwards(

        val id: String,

        val awardId: String
)