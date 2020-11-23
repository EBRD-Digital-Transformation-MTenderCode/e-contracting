package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.CreateFrameworkContractResult
import com.procurement.contracting.domain.util.extension.asString
import com.procurement.contracting.infrastructure.handler.v2.model.response.CreateFrameworkContractResponse

fun CreateFrameworkContractResult.convert() = CreateFrameworkContractResponse(
    token = token.underlying.toString(),
    contracts = listOf(
        CreateFrameworkContractResponse.Contract(
            id = id.underlying,
            status = status.key,
            statusDetails = statusDetails.key,
            date = date.asString(),
            isFrameworkOrDynamic = isFrameworkOrDynamic
        )
    )
)
