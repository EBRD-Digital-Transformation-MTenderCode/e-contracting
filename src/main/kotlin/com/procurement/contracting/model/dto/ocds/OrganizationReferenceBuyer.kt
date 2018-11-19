package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OrganizationReferenceBuyer @JsonCreator constructor(

        var id: String,

        val name: String,

        val additionalIdentifiers: java.util.HashSet<Identifier>,

        val persones: java.util.HashSet<Person>,

        val details: DetailsBuyer
)

