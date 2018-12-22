package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ContractedAward @JsonCreator constructor(

        val id: String,

        var date: LocalDateTime,

        var description: String? = null,

        var title: String? = null,

        var value: ValueTax,

        val relatedLots: List<String>,

        val relatedBids: List<String>,

        val relatedAwards: List<String>? = null,

        var items: List<Item>,

        var documents: List<DocumentAward>?,

        var suppliers: List<OrganizationReferenceSupplier>
)