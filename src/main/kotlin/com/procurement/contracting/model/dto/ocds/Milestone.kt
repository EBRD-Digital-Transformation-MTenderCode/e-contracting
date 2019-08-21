package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.domain.model.item.ItemId
import com.procurement.contracting.domain.model.milestone.status.MilestoneStatus
import com.procurement.contracting.domain.model.milestone.type.MilestoneSubType
import com.procurement.contracting.domain.model.milestone.type.MilestoneType
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Milestone @JsonCreator constructor(

    var id: String,

    var title: String,

    var description: String,

    val type: MilestoneType,

    val subtype: MilestoneSubType? = null,

    var status: MilestoneStatus?,

    var relatedItems: Set<ItemId>?,

    var additionalInformation: String?,

    var dueDate: LocalDateTime,

    var relatedParties: List<RelatedParty>? = null,

    var dateModified: LocalDateTime? = null,

    var dateMet: LocalDateTime? = null
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class RelatedParty @JsonCreator constructor(

    val id: String,

    val name: String
)
