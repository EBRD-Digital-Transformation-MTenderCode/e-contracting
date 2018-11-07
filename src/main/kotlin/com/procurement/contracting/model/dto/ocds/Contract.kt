package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Contract @JsonCreator constructor(

        val token: String?,

        val id: String,

        val date: LocalDateTime?,

        val awardId: String,

        val status: ContractStatus,

        val statusDetails: ContractStatusDetails,

        val title: String?,

        val description: String?,

        val extendsContractID: String?,

        val budgetSource: List<BudgetSource>?,

        val classification: Classification?,

        val period: Period?,

        val value: Value?,

        val items: List<Item>?,

        val dateSigned: LocalDateTime?,

        val documents: List<Document>?,

        val relatedProcesses: List<RelatedProcess>?,

        val amendments: List<Amendment>?
)