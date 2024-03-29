package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.SetStateForContractsParams
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.infrastructure.handler.v2.model.request.SetStateForContractsRequest
import com.procurement.contracting.lib.functional.Result

fun SetStateForContractsRequest.convert(): Result<SetStateForContractsParams, DataErrors> {
    val convertedTender = tender?.convert()?.onFailure { return it }

    return SetStateForContractsParams.tryCreate(
        cpid = cpid,
        ocid = ocid,
        pmd = pmd,
        country = country,
        operationType = operationType,
        tender = convertedTender,
        contracts = contracts?.map { SetStateForContractsParams.Contract(it.id) }
    )
}

fun SetStateForContractsRequest.Tender.convert(): Result<SetStateForContractsParams.Tender, DataErrors> {
    val convertedLots = lots.map { it.convert() }
    return SetStateForContractsParams.Tender.tryCreate(lots = convertedLots)
}

fun SetStateForContractsRequest.Tender.Lot.convert(): SetStateForContractsParams.Tender.Lot =
    SetStateForContractsParams.Tender.Lot(id = id)
