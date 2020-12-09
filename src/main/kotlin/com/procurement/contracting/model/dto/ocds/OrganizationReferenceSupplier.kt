package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.domain.model.organization.OrganizationId

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OrganizationReferenceSupplier @JsonCreator constructor(

    var id: OrganizationId,

    val name: String,

    val identifier: Identifier,

    val address: Address,

    val contactPoint: ContactPoint,

    var additionalIdentifiers: List<Identifier>?,

    var persones: List<Person>?,

    var details: DetailsSupplier?
)
