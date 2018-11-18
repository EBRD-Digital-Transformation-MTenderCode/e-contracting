package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*


@JsonInclude(JsonInclude.Include.NON_NULL)
data class AgreedMetric @JsonCreator constructor(

        var id: String,

        val title: String,

        val description: String,

        var observations: LinkedList<Observation>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Observation @JsonCreator constructor(

        val id: String,

        val notes: String,

        val measure: Any,

        val unit: ObservationUnit?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ObservationUnit @JsonCreator constructor(

        val id: String,

        val name: String,

        val scheme: String
)