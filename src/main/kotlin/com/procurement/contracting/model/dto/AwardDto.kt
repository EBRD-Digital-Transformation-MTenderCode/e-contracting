package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator

data class AwardDto @JsonCreator constructor(
        val awardId: String
)