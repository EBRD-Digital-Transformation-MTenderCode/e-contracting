package com.procurement.contracting.infrastructure.handler.v1.model.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.contracting.domain.model.award.AwardId

data class AwardDto @JsonCreator constructor(
    val awardId: AwardId
)
