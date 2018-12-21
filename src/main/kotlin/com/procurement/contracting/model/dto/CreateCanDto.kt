package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.model.dto.ocds.Can

data class CanCreate @JsonCreator constructor(

        val award: AwardCanCreate
)

data class AwardCanCreate @JsonCreator constructor(

        val id: String,

        val relatedLots: List<String>
)


@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreateCanRs(

        val can: Can
)