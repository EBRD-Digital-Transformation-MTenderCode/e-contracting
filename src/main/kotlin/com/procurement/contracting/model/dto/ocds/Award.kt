package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Award @JsonCreator constructor(

        val id: String,

        var date: LocalDateTime,

        var description: String?,

        var title: String? = null,

        val relatedLots: List<String>,

        val relatedBid: String,

        var value: ValueTax,

        var items: List<Item>,

        var documents: List<DocumentAward>?,

        var suppliers: List<OrganizationReferenceSupplier>
)