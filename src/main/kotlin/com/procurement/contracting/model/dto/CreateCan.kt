package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.model.dto.ocds.Award
import com.procurement.contracting.model.dto.ocds.Can

data class CanCreate @JsonCreator constructor(

        val awards: List<AwardCanCreate>
)

data class AwardCanCreate @JsonCreator constructor(

        val id: String
)


@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreateCanRs(

        val cans: List<Can>?
)