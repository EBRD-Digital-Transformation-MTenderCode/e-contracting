package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.procurement.contracting.model.dto.databinding.MoneyDeserializer
import com.procurement.contracting.model.dto.databinding.QuantityDeserializer
import com.procurement.contracting.model.dto.ocds.*
import com.procurement.contracting.model.dto.ocds.Unit
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateAcRq @JsonCreator constructor(

        val awards: List<AwardCreate>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AwardCreate @JsonCreator constructor(

        val id: String,

        var date: LocalDateTime,

        var description: String,

        var status: AwardStatus,

        var statusDetails: AwardStatusDetails,

        var value: ValueCreate,

        val relatedLots: List<String>,

        val relatedBid: String,

        var suppliers: List<OrganizationReferenceSupplier>,

        var documents: List<DocumentAward>?,

        var items: List<ItemCreate>?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ValueCreate @JsonCreator constructor(

        @JsonDeserialize(using = MoneyDeserializer::class)
        val amount: BigDecimal,

        val currency: String
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ItemCreate @JsonCreator constructor(

        val id: String,

        val description: String?,

        val classification: Classification,

        val additionalClassifications: Set<Classification>?,

        @JsonDeserialize(using = QuantityDeserializer::class)
        val quantity: BigDecimal,

        val unit: Unit,

        val relatedLot: String
)


@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreateAcRs(

        val cans: List<Can>?,

        val contracts: List<Contract>?
)