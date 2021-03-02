package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.CheckContractStateParams
import com.procurement.contracting.domain.util.extension.mapResult
import com.procurement.contracting.infrastructure.handler.v2.model.request.CheckContractStateRequest

fun CheckContractStateRequest.convert() =
    CheckContractStateParams.tryCreate(
        cpid = cpid,
        ocid = ocid,
        pmd = pmd,
        country = country,
        operationType = operationType,
        contracts = contracts.mapResult { it.convert() }.onFailure { return it }
    )

fun CheckContractStateRequest.Contract.convert() =
    CheckContractStateParams.Contract.tryCreate(id = id)
