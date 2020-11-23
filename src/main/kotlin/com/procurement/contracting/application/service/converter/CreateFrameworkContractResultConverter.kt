package com.procurement.contracting.application.service.converter

import com.procurement.contracting.application.service.model.CreateFrameworkContractResult
import com.procurement.contracting.domain.model.fc.FrameworkContract

fun FrameworkContract.convert() = CreateFrameworkContractResult(
    token = token,
    id = id,
    status = status,
    statusDetails = statusDetails,
    date = date,
    isFrameworkOrDynamic = isFrameworkOrDynamic
)
