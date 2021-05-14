package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.AddGeneratedDocumentToContractParams
import com.procurement.contracting.domain.util.extension.mapResult
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.infrastructure.handler.v2.model.request.AddGeneratedDocumentToContractRequest
import com.procurement.contracting.lib.functional.Result

fun AddGeneratedDocumentToContractRequest.convert(): Result<AddGeneratedDocumentToContractParams, DataErrors> =
    AddGeneratedDocumentToContractParams.tryCreate(
        cpid = cpid,
        ocid = ocid,
        processInitiator = processInitiator,
        contracts = contracts
            .mapResult { it.convert() }
            .onFailure { return it }
    )

fun AddGeneratedDocumentToContractRequest.Contract.convert() =
    AddGeneratedDocumentToContractParams.Contract.tryCreate(id = this.id, documents = documents.map { it.convert() })

fun AddGeneratedDocumentToContractRequest.Contract.Document.convert() =
    AddGeneratedDocumentToContractParams.Contract.Document(id = id)
