package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.CheckExistenceSupplierReferencesInFCParams
import com.procurement.contracting.domain.util.extension.mapResult
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.infrastructure.handler.v2.model.request.CheckExistenceSupplierReferencesInFCRequest
import com.procurement.contracting.lib.functional.Result

fun CheckExistenceSupplierReferencesInFCRequest.convert(): Result<CheckExistenceSupplierReferencesInFCParams, DataErrors> {
    val contracts = contracts.mapResult { it.convert() }.onFailure { return it }
    return CheckExistenceSupplierReferencesInFCParams.tryCreate(cpid = cpid, ocid = ocid, contracts = contracts)
}

fun CheckExistenceSupplierReferencesInFCRequest.Contract.convert(): Result<CheckExistenceSupplierReferencesInFCParams.Contract, DataErrors> =
    CheckExistenceSupplierReferencesInFCParams.Contract.tryCreate(id = id)

