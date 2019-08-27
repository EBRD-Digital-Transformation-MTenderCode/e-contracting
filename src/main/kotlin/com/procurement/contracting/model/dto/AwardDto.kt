package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.contracting.domain.model.award.AwardId

data class AwardDto @JsonCreator constructor(
    val awardId: AwardId
)
