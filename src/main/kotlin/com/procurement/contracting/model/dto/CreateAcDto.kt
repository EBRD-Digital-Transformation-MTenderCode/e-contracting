package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.procurement.contracting.model.dto.databinding.MoneyDeserializer
import com.procurement.contracting.model.dto.databinding.QuantityDeserializer
import com.procurement.contracting.model.dto.ocds.*
import com.procurement.contracting.model.dto.ocds.Unit
import java.math.BigDecimal
import java.time.LocalDateTime


data class CreateAcRq @JsonCreator constructor(

        val contracts: List<ContractCreateAc>,

        val awards: List<Award>,

        val contractedTender: GetDataForAcTender
)

data class ContractCreateAc @JsonCreator constructor(

        var id: String
)

data class GetDataForAcTender @JsonCreator constructor(

        val mainProcurementCategory: MainProcurementCategory,

        var items: List<Item>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreateAcRs(

        val cans: List<Can>,

        val contract: Contract,

        val contractedAward: Award
)