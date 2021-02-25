package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.AddGeneratedDocumentToContractParams
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.infrastructure.handler.v2.model.request.AddGeneratedDocumentToContractRequest
import com.procurement.contracting.lib.functional.Result

fun AddGeneratedDocumentToContractRequest.convert(): Result<AddGeneratedDocumentToContractParams, DataErrors> =
    AddGeneratedDocumentToContractParams.tryCreate(
        cpid = cpid,
        ocid = ocid,
        documentInitiator = documentInitiator,
        contracts = contracts.map { it.convert() }
    )

fun AddGeneratedDocumentToContractRequest.Contract.convert() =
    AddGeneratedDocumentToContractParams.Contract(documents = documents.map { it.convert() })

fun AddGeneratedDocumentToContractRequest.Contract.Document.convert() =
    AddGeneratedDocumentToContractParams.Contract.Document(id = id)
