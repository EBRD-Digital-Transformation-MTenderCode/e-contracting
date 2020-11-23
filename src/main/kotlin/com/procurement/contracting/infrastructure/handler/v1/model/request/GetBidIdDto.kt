package com.procurement.contracting.infrastructure.handler.v1.model.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.domain.model.bid.BidId

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GetBidIdRs(
    val relatedBids: List<BidId>
)
