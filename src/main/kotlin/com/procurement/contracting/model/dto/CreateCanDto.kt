package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.model.dto.ocds.Can

data class AwardDto @JsonCreator constructor(

        val awardId: String
)

data class CreateCanRs(

        val can: Can
)