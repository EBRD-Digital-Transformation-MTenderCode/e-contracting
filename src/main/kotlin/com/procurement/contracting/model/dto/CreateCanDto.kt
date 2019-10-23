package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.model.dto.ocds.Can

data class CreateCanRq @JsonCreator constructor(

    val awardingSuccess: Boolean,

    val awardId: AwardId?
)

data class CreateCanRs(

    val can: Can
)
