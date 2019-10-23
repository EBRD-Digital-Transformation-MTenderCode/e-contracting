package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.domain.model.bid.BidId

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GetBidIdRs(
    val relatedBids: List<BidId>
)
