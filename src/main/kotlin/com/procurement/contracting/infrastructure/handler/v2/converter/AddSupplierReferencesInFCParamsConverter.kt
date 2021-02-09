package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.AddSupplierReferencesInFCParams
import com.procurement.contracting.domain.model.parseCpid
import com.procurement.contracting.domain.model.parseOcid
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.infrastructure.handler.v2.model.request.AddSupplierReferencesInFCRequest
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess

fun AddSupplierReferencesInFCRequest.convert(): Result<AddSupplierReferencesInFCParams, DataErrors> {
    val cpid = parseCpid(value = cpid).onFailure { return it }
    val ocid = parseOcid(value = ocid).onFailure { return it }
    val parties = parties.map { it.convert() }

    return AddSupplierReferencesInFCParams(cpid = cpid, ocid = ocid, parties = parties).asSuccess()
}

fun AddSupplierReferencesInFCRequest.Party.convert(): AddSupplierReferencesInFCParams.Party =
    AddSupplierReferencesInFCParams.Party(id = id, name = name)
