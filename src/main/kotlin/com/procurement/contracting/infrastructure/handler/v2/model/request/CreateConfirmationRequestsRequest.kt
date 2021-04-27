package com.procurement.contracting.infrastructure.handler.v2.model.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class CreateConfirmationRequestsRequest(
    @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: String,
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: String,
    @field:JsonProperty("role") @param:JsonProperty("role") val role: String,
    @field:JsonProperty("contracts") @param:JsonProperty("contracts") val contracts: List<Contract>,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("access") @param:JsonProperty("access") val access: Access?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("dossier") @param:JsonProperty("dossier") val dossier: Dossier?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("submission") @param:JsonProperty("submission") val submission: Submission?,
) {
    data class Contract(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document>?
    ) {
        data class Document(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String
        )
    }

    data class Access(
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("buyers") @param:JsonProperty("buyers") val buyers: List<Buyer>?,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("procuringEntity") @param:JsonProperty("procuringEntity") val procuringEntity: ProcuringEntity?
    ) {
        data class Buyer(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
            @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: String
        )

        data class ProcuringEntity(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
            @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: String
        )
    }

    data class Dossier(
        @field:JsonProperty("candidates") @param:JsonProperty("candidates") val candidates: List<Candidate>
    ) {
        data class Candidate(
            @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: String,
            @field:JsonProperty("organizations") @param:JsonProperty("organizations") val organizations: List<Organization>
        ) {
            data class Organization(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("name") @param:JsonProperty("name") val name: String
            )
        }
    }

    data class Submission(
        @field:JsonProperty("tenderers") @param:JsonProperty("tenderers") val tenderers: List<Tenderer>
    ) {
        data class Tenderer(
            @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: String,
            @field:JsonProperty("organizations") @param:JsonProperty("organizations") val organizations: List<Organization>
        ) {
            data class Organization(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("name") @param:JsonProperty("name") val name: String
            )
        }
    }

}
