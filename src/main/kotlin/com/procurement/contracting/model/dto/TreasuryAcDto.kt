package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.contracting.model.dto.ocds.Contract


data class TreasuryAcRs @JsonCreator constructor(

        val contract: Contract
)