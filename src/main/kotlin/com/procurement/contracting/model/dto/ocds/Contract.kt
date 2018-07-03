package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime
import javax.validation.Valid
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Contract @JsonCreator constructor(

        val token: String?,

        @field:NotNull
        val id: String,

        val date: LocalDateTime?,

        @field:NotNull
        val awardId: String,

        @field:Valid
        @field:NotNull
        val status: ContractStatus,

        @field:Valid
        @field:NotNull
        val statusDetails: ContractStatusDetails,

        @field:NotNull
        val title: String?,

        @field:NotNull
        val description: String?,

        @field:NotNull
        val extendsContractID: String?,

        @field:Valid
        val budgetSource: List<BudgetSource>?,

        @field:Valid
        @field:NotNull
        val classification: Classification?,

        @field:Valid
        @field:NotNull
        val period: Period?,

        @field:Valid
        @field:NotNull
        val value: Value?,

        @field:Valid
        val items: List<Item>?,

        val dateSigned: LocalDateTime?,

        @field:Valid
        val documents: List<Document>?,

        @field:Valid
        @field:NotNull
        val relatedProcesses: List<RelatedProcess>?,

        @field:Valid
        @field:NotNull
        val amendments: List<Amendment>?
)