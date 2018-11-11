package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.procurement.contracting.model.dto.databinding.MoneyDeserializer
import java.math.BigDecimal
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Award @JsonCreator constructor(

        val id: String,

        var date: LocalDateTime,

        var description: String,

        var title: String? = null,

        var status: AwardStatus,

        var statusDetails: AwardStatusDetails,

        var value: ValueAward,

        val relatedLots: List<String>,

        val relatedBid: String,

        var suppliers: List<OrganizationReferenceSupplier>,

        var items: List<Item>,

        var documents: List<Document>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ValueAward @JsonCreator constructor(

        @JsonDeserialize(using = MoneyDeserializer::class)
        val amount: BigDecimal,

        val currency: String,

        @JsonDeserialize(using = MoneyDeserializer::class)
        val amountNet: BigDecimal?,

        val valueAddedTaxIncluded: Boolean?
)