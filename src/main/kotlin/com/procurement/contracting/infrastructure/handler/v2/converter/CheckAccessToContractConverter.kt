package com.procurement.contracting.infrastructure.handler.v2.base

import com.procurement.contracting.application.service.model.CheckAccessToContractParams
import com.procurement.contracting.domain.model.parseCpid
import com.procurement.contracting.domain.model.parseOcid
import com.procurement.contracting.domain.model.parseOwner
import com.procurement.contracting.domain.model.parseToken
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.infrastructure.handler.v2.converter.rule.notEmptyRule
import com.procurement.contracting.infrastructure.handler.v2.model.request.CheckAccessToContractRequest
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess
import com.procurement.contracting.lib.functional.validate

fun CheckAccessToContractRequest.convert(): Result<CheckAccessToContractParams, DataErrors> {

    val cpidParsed = parseCpid(value = cpid)
        .onFailure { error -> return error }

    val ocidParsed = parseOcid(value = ocid)
        .onFailure { error -> return error }

    val token = parseToken(token)
        .onFailure { error -> return error }

    val owner = parseOwner(owner)
        .onFailure { error -> return error }

    contracts.validate(notEmptyRule("contracts"))
        .onFailure { return it }

    return CheckAccessToContractParams(
        cpid = cpidParsed,
        ocid = ocidParsed,
        contracts = contracts
            .map { CheckAccessToContractParams.Contract(it.id) },
        token = token,
        owner = owner
    ).asSuccess()
}

