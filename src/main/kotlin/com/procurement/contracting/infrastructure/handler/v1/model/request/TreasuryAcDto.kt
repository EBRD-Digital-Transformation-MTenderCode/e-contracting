package com.procurement.contracting.infrastructure.handler.v1.model.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.contracting.model.dto.ocds.AwardContract


data class TreasuryAcRs @JsonCreator constructor(

        val contract: AwardContract
)