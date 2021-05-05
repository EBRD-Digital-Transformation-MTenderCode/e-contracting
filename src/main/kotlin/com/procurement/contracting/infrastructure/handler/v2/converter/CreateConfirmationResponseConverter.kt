package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.domain.util.extension.mapResult
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.application.service.model.CreateConfirmationResponseParams as Params
import com.procurement.contracting.infrastructure.handler.v2.model.request.CreateConfirmationResponseRequest as Request

fun Request.convert(): Result<Params, DataErrors> {
    val contracts = contracts
        .mapResult { it.convert() }
        .onFailure { return it }

    return Params.tryCreate(cpid = cpid, ocid = ocid, contracts = contracts, date = date)
}

fun Request.Contract.convert(): Result<Params.Contract, DataErrors> {
    val confirmationResponses = confirmationResponses
        .mapResult { it.convert() }
        .onFailure { return it }

    return Params.Contract.tryCreate(id = id, confirmationResponses = confirmationResponses)
}

fun Request.Contract.ConfirmationResponse.convert(): Result<Params.Contract.ConfirmationResponse, DataErrors> {
    val relatedPerson = relatedPerson.convert().onFailure { return it }

    return Params.Contract.ConfirmationResponse.tryCreate(
        id = id,
        requestId = requestId,
        type = type,
        value = value,
        relatedPerson = relatedPerson
    )
}

fun Request.Contract.ConfirmationResponse.Person.convert(): Result<Params.Contract.ConfirmationResponse.Person, DataErrors> {
    val businessFunctions = businessFunctions
        .mapResult { it.convert() }
        .onFailure { return it }

    return Params.Contract.ConfirmationResponse.Person.tryCreate(
        id = id,
        title = title,
        name = name,
        identifier = identifier.convert(),
        businessFunctions = businessFunctions
    )
}

fun Request.Contract.ConfirmationResponse.Person.Identifier.convert() =
    Params.Contract.ConfirmationResponse.Person.Identifier(id = id, scheme = scheme, uri = uri)

fun Request.Contract.ConfirmationResponse.Person.BusinessFunction.convert(): Result<Params.Contract.ConfirmationResponse.Person.BusinessFunction, DataErrors> {
    val period = period.convert().onFailure { return it.reason.asFailure() }

    val documents = documents
        ?.mapResult { it.convert() }
        ?.onFailure { return it }

    return Params.Contract.ConfirmationResponse.Person.BusinessFunction.tryCreate(
        id = id,
        type = type,
        jobTitle = jobTitle,
        period = period,
        documents = documents
    )
}

fun Request.Contract.ConfirmationResponse.Person.BusinessFunction.Period.convert() =
    Params.Contract.ConfirmationResponse.Person.BusinessFunction.Period.tryCreate(startDate = startDate)

fun Request.Contract.ConfirmationResponse.Person.BusinessFunction.Document.convert() =
    Params.Contract.ConfirmationResponse.Person.BusinessFunction.Document.tryCreate(
        id = id,
        documentType = documentType,
        title = title,
        description = description
    )
