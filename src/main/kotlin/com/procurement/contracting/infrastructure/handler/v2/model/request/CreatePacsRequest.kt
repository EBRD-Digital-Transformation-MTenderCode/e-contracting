package com.procurement.contracting.infrastructure.handler.v2.model.request


import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.DynamicValue

data class CreatePacsRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: String,
    @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender,
    @param:JsonProperty("date") @field:JsonProperty("date") val date: String,
    @param:JsonProperty("awards") @field:JsonProperty("awards") val awards: List<Award>?,
    @param:JsonProperty("bids") @field:JsonProperty("bids") val bids: Bids?,
    @param:JsonProperty("owner") @field:JsonProperty("owner") val owner: String
) {
    data class Tender(
        @param:JsonProperty("lots") @field:JsonProperty("lots") val lots: List<Lot>,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("targets") @field:JsonProperty("targets") val targets: List<Target>?,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("criteria") @field:JsonProperty("criteria") val criteria: List<Criteria>?
    ) {
        data class Lot(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String
        )

        data class Target(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("observations") @field:JsonProperty("observations") val observations: List<Observation>
        ) {
            data class Observation(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("unit") @field:JsonProperty("unit") val unit: Unit,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("relatedRequirementId") @field:JsonProperty("relatedRequirementId") val relatedRequirementId: String?
            ) {
                data class Unit(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                )
            }
        }

        data class Criteria(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("title") @field:JsonProperty("title") val title: String,
            @param:JsonProperty("relatesTo") @field:JsonProperty("relatesTo") val relatesTo: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("relatedItem") @field:JsonProperty("relatedItem") val relatedItem: String?,

            @param:JsonProperty("requirementGroups") @field:JsonProperty("requirementGroups") val requirementGroups: List<RequirementGroup>
        ) {
            data class RequirementGroup(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("requirements") @field:JsonProperty("requirements") val requirements: List<Requirement>
            ) {
                data class Requirement(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("title") @field:JsonProperty("title") val title: String
                )
            }
        }
    }

    data class Award(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
        @param:JsonProperty("suppliers") @field:JsonProperty("suppliers") val suppliers: List<Supplier>
    ) {
        data class Supplier(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("name") @field:JsonProperty("name") val name: String
        )
    }

    data class Bids(
        @param:JsonProperty("details") @field:JsonProperty("details") val details: List<Detail>
    ) {
        data class Detail(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("tenderers") @field:JsonProperty("tenderers") val tenderers: List<Tenderer>,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("requirementResponses") @field:JsonProperty("requirementResponses") val requirementResponses: List<RequirementResponse>?
        ) {
            data class Tenderer(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String
            )

            data class RequirementResponse(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("value") @field:JsonProperty("value") val value: DynamicValue,
                @param:JsonProperty("requirement") @field:JsonProperty("requirement") val requirement: Requirement,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("period") @field:JsonProperty("period") val period: Period?
            ) {
                data class Requirement(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                )

                data class Period(
                    @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: String,
                    @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: String
                )
            }
        }
    }
}