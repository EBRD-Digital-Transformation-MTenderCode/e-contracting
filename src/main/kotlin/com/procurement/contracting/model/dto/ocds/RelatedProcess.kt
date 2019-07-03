package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.domain.model.related.process.RelatedProcessScheme
import com.procurement.contracting.domain.model.related.process.RelatedProcessType

@JsonInclude(JsonInclude.Include.NON_NULL)
data class RelatedProcess @JsonCreator constructor(

        val id: String?,

        val relationship: List<RelatedProcessType>?,

        val title: String?,

        val scheme: RelatedProcessScheme?,

        val identifier: String?,

        val uri: String?
)

