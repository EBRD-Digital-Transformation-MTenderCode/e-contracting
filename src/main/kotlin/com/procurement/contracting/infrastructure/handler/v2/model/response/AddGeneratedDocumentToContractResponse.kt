package com.procurement.contracting.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.fc.FrameworkContract

data class AddGeneratedDocumentToContractResponse(
    @field:JsonProperty("contracts") @param:JsonProperty("contracts") val contracts: List<Contract>
) {
    data class Contract(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("status") @param:JsonProperty("status") val status: String,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String,
        @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document>
    ) { companion object {}

        data class Document(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: String
        ) { companion object {} }
    }
}

fun AddGeneratedDocumentToContractResponse.Contract.Companion.fromDomain(contract: FrameworkContract) =
    AddGeneratedDocumentToContractResponse.Contract(
        id = contract.id.underlying,
        status = contract.status.key,
        statusDetails = contract.statusDetails.key,
        documents = contract.documents.map {
            AddGeneratedDocumentToContractResponse.Contract.Document.fromDomain(it)
        }
    )

fun AddGeneratedDocumentToContractResponse.Contract.Document.Companion.fromDomain(document: FrameworkContract.Document) =
    AddGeneratedDocumentToContractResponse.Contract.Document(
        id = document.id,
        documentType = document.documentType.key
    )
