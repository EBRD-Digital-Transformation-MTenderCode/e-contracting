package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator

data class RelatedProcess @JsonCreator constructor(

        val id: String?,

        val relationship: List<RelatedProcessType>?,

        val title: String?,

        val scheme: RelatedProcessScheme?,

        val identifier: String?,

        val uri: String?
)

