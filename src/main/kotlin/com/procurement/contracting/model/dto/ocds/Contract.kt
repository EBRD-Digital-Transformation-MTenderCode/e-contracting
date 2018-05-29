package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.contracting.model.dto.databinding.JsonDateDeserializer
import com.procurement.contracting.model.dto.databinding.JsonDateSerializer
import java.time.LocalDateTime
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Contract(

        @JsonProperty("token")
        val token: String?,

        @JsonProperty("id") @NotNull
        val id: String,

        @JsonProperty("date")
        @JsonDeserialize(using = JsonDateDeserializer::class)
        @JsonSerialize(using = JsonDateSerializer::class)
        val date: LocalDateTime?,

        @JsonProperty("awardId") @NotNull
        val awardId: String,

        @JsonProperty("status") @Valid @NotNull
        val status: ContractStatus,

        @JsonProperty("statusDetails") @Valid @NotNull
        val statusDetails: ContractStatusDetails,

        @JsonProperty("title") @NotNull
        val title: String?,

        @JsonProperty("description") @NotNull
        val description: String?,

        @JsonProperty("extendsContractID") @NotNull
        val extendsContractID: String?,

        @JsonProperty("budgetSource") @Valid @NotEmpty
        val budgetSource: List<BudgetSource>?,

        @JsonProperty("classification") @Valid @NotNull
        val classification: Classification?,

        @JsonProperty("period") @Valid @NotNull
        val period: Period?,

        @JsonProperty("value") @Valid @NotNull
        val value: Value?,

        @JsonProperty("items") @Valid @NotEmpty
        val items: List<Item>?,

        @JsonProperty("dateSigned")
        @JsonDeserialize(using = JsonDateDeserializer::class)
        @JsonSerialize(using = JsonDateSerializer::class)
        val dateSigned: LocalDateTime?,

        @JsonProperty("documents") @Valid
        val documents: List<Document>?,

        @JsonProperty("relatedProcesses") @Valid @NotNull
        val relatedProcesses: List<RelatedProcess>?,

        @JsonProperty("amendments") @Valid @NotNull
        val amendments: List<Amendment>?
)