package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.domain.model.organization.OrganizationId

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OrganizationReferenceBuyer @JsonCreator constructor(

    var id: OrganizationId,

    val name: String?,

    val identifier: Identifier?,

    val address: Address?,

    val contactPoint: ContactPoint?,

    val additionalIdentifiers: HashSet<Identifier>,

    val persones: HashSet<Person>,

    val details: DetailsBuyer
)
