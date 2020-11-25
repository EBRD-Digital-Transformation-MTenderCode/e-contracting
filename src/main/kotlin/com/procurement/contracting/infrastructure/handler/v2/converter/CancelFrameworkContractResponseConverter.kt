package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.CancelFrameworkContractResult
import com.procurement.contracting.infrastructure.handler.v2.model.response.CancelFrameworkContractResponse

fun CancelFrameworkContractResult.convert() = CancelFrameworkContractResponse(
    contracts = listOf(
        CancelFrameworkContractResponse.Contract(
            id = id.underlying,
            status = status.key,
            statusDetails = statusDetails.key
        )
    )
)
