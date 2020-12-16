package com.procurement.contracting.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class CreatePacsResponse(
    @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @param:JsonProperty("token") @field:JsonProperty("token") val token: String?
) {
    data class Contract(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
        @param:JsonProperty("status") @field:JsonProperty("status") val status: String,
        @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: String,
        @param:JsonProperty("date") @field:JsonProperty("date") val date: String,
        @param:JsonProperty("relatedLots") @field:JsonProperty("relatedLots") val relatedLots: List<String>,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("suppliers") @field:JsonProperty("suppliers") val suppliers: List<Supplier>?,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("awardId") @field:JsonProperty("awardId") val awardId: String?,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("agreedMetrics ") @field:JsonProperty("agreedMetrics ") val agreedMetrics: List<AgreedMetric>?
    ) {
        data class Supplier(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("name") @field:JsonProperty("name") val name: String
        )

        data class AgreedMetric(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("title") @field:JsonProperty("title") val title: String,
            @param:JsonProperty("observations") @field:JsonProperty("observations") val observations: List<Observation>
        ) {
            data class Observation(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("notes") @field:JsonProperty("notes") val notes: String,
                @param:JsonProperty("measure") @field:JsonProperty("measure") val measure: Any,
                @param:JsonProperty("relatedRequirementId") @field:JsonProperty("relatedRequirementId") val relatedRequirementId: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("period") @field:JsonProperty("period") val period: Period?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("unit") @field:JsonProperty("unit") val unit: Unit?
            ) {
                data class Period(
                    @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: String,
                    @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: String
                )

                data class Unit(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                )
            }
        }
    }
}