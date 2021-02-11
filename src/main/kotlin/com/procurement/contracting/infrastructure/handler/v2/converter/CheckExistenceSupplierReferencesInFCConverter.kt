package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.CheckExistenceSupplierReferencesInFCParams
import com.procurement.contracting.infrastructure.handler.v2.model.request.CheckExistenceSupplierReferencesInFCRequest

fun CheckExistenceSupplierReferencesInFCRequest.convert() =
    CheckExistenceSupplierReferencesInFCParams.tryCreate(
        cpid = cpid,
        ocid = ocid,
    )
