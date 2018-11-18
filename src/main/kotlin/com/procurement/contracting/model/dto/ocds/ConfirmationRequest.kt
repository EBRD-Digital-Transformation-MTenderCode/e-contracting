package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ConfirmationRequest @JsonCreator constructor(

        var id: String,

        var type: String?,

        var title: String?,

        var description: String?,

        var relatesTo: String?,

        val relatedItem: String,

        val source: String,

        var requestGroups: Set<RequestGroup>?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class RequestGroup @JsonCreator constructor(

        val id: String,

        val requests: Set<Request>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Request @JsonCreator constructor(

        val id: String,

        val title: String,

        val description: String,

        val relatedPerson: RelatedPerson?
)


@JsonInclude(JsonInclude.Include.NON_NULL)
data class RelatedPerson @JsonCreator constructor(

        val id: String,

        val name: String
)
