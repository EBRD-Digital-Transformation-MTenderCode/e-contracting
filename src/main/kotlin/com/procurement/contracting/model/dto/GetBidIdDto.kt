package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GetBidIdRs(

        val relatedBids: List<String>
)