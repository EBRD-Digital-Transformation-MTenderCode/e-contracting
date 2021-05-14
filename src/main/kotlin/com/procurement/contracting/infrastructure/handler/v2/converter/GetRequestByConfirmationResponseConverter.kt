package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.domain.util.extension.mapResult
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.application.service.model.GetRequestByConfirmationResponseParams as Params
import com.procurement.contracting.infrastructure.handler.v2.model.request.GetRequestByConfirmationResponseRequest as Request

fun Request.convert(): Result<Params, Fail> {
    val contracts = contracts
        .mapResult { it.convert() }
        .onFailure { return it }

    return Params.tryCreate(cpid = cpid, ocid = ocid, contracts = contracts)
}

fun Request.Contract.convert(): Result<Params.Contract, Fail> {
    val confirmationResponses = confirmationResponses
        .mapResult { it.convert() }
        .onFailure { return it }

    return Params.Contract.tryCreate(id = id, confirmationResponses = confirmationResponses)
}

fun Request.Contract.ConfirmationResponse.convert(): Result<Params.Contract.ConfirmationResponse, Fail> =
    Params.Contract.ConfirmationResponse.tryCreate(id = id, requestId)

