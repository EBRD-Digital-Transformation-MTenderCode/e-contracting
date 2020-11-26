package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.domain.model.item.ItemId
import com.procurement.contracting.domain.model.lot.LotId
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Item @JsonCreator constructor(

    val id: ItemId,

    val internalId: String?,

    val description: String?,

    val classification: Classification,

    val additionalClassifications: List<Classification>?,

    var quantity: BigDecimal,

    val unit: Unit,

    val relatedLot: LotId,

    var deliveryAddress: Address?
)
