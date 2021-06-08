package com.procurement.contracting.infrastructure.handler.v2.base

import com.procurement.contracting.application.service.model.GetCanByIdsParams
import com.procurement.contracting.domain.model.parseCpid
import com.procurement.contracting.domain.model.parseOcid
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.infrastructure.handler.v2.converter.rule.notEmptyRule
import com.procurement.contracting.infrastructure.handler.v2.model.request.GetCanByIdsRequest
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess
import com.procurement.contracting.lib.functional.validate

fun GetCanByIdsRequest.convert(): Result<GetCanByIdsParams, DataErrors> {

    val cpidParsed = parseCpid(value = cpid)
        .onFailure { error -> return error }

    val ocidParsed = parseOcid(value = ocid)
        .onFailure { error -> return error }

    contracts.validate(notEmptyRule("contracts"))
        .onFailure { return it }

    return GetCanByIdsParams(
        cpid = cpidParsed,
        ocid = ocidParsed,
        contracts = contracts
            .map { GetCanByIdsParams.Contract(it.id) }
    ).asSuccess()
}

