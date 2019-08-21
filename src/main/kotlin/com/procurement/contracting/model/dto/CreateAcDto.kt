package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.model.dto.ocds.*


data class CreateAcRq @JsonCreator constructor(

        val contracts: List<ContractCreateAc>,

        val awards: List<Award>,

        val contractedTender: GetDataForAcTender
)

data class ContractCreateAc @JsonCreator constructor(

        var id: CANId
)

data class GetDataForAcTender @JsonCreator constructor(

        val mainProcurementCategory: String,

        var items: HashSet<Item>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreateAcRs(

        val cans: List<Can>,

        val contract: Contract,

        val contractedAward: ContractedAward
)