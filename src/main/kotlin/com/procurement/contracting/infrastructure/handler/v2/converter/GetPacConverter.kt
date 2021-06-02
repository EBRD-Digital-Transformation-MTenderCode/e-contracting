package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.errors.GetPacErrors
import com.procurement.contracting.application.service.model.GetPacParams
import com.procurement.contracting.domain.model.parsePACId
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.v2.model.request.GetPacRequest
import com.procurement.contracting.lib.functional.Result

fun GetPacRequest.convert(): Result<GetPacParams, Fail> {
    val contract = contracts.singleOrNull() ?: return Result.failure(GetPacErrors.UnexpectedIdentifiers())


    return GetPacParams.tryCreate(
        cpid = cpid,
        ocid = ocid,
        contracts = listOf(
            GetPacParams.Contract(
                id = parsePACId(contract.id, "contracts.id")
                    .onFailure { return it })
        )
    )
}

