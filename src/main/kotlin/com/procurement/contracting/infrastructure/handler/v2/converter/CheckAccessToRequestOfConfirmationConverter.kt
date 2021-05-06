package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.CheckAccessToRequestOfConfirmationParams
import com.procurement.contracting.domain.util.extension.mapResult
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.infrastructure.handler.v2.model.request.CheckAccessToRequestOfConfirmationRequest
import com.procurement.contracting.lib.functional.Result

fun CheckAccessToRequestOfConfirmationRequest.convert() =
    CheckAccessToRequestOfConfirmationParams.tryCreate(
        cpid = cpid,
        ocid = ocid,
        token = token,
        owner = owner,
        contracts = contracts
            .mapResult { it.convert() }
            .onFailure { return it }
    )

fun CheckAccessToRequestOfConfirmationRequest.Contract.convert(): Result<CheckAccessToRequestOfConfirmationParams.Contract, DataErrors> {
    val confirmationResponses = confirmationResponses
        .mapResult { it.convert() }
        .onFailure { return it }

    return CheckAccessToRequestOfConfirmationParams.Contract.tryCreate(id = id, confirmationResponses = confirmationResponses)
}

fun CheckAccessToRequestOfConfirmationRequest.Contract.ConfirmationResponse.convert() =
    CheckAccessToRequestOfConfirmationParams.Contract.ConfirmationResponse.tryCreate(id = id, requestId = requestId)