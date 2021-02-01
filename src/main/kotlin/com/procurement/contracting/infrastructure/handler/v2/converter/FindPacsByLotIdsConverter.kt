package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.FindPacsByLotIdsParams
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.infrastructure.handler.v2.model.request.FindPacsByLotIdsRequest
import com.procurement.contracting.lib.functional.Result

fun FindPacsByLotIdsRequest.convert(): Result<FindPacsByLotIdsParams, DataErrors.Validation> {
    val tender = tender.convert().onFailure { return it }

    return FindPacsByLotIdsParams.tryCreate(
        cpid = cpid,
        ocid = ocid,
        tender = tender
    )
}

fun FindPacsByLotIdsRequest.Tender.convert() =
    FindPacsByLotIdsParams.Tender.tryCreate(
        lots = lots.map { it.convert() }
    )

fun FindPacsByLotIdsRequest.Tender.Lot.convert() =
    FindPacsByLotIdsParams.Tender.Lot(id = id)