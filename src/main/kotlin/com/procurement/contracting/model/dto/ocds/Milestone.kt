package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Milestone @JsonCreator constructor(

        var id: String,

        var title: String,

        var description: String,

        val type: MilestoneType,

        var status: MilestoneStatus?,

        var relatedItems: Set<String>?,

        var additionalInformation: String,

        var dueDate: LocalDateTime,

        var relatedParties: RelatedParty?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class RelatedParty @JsonCreator constructor(

        val id: String,

        val name: String
)