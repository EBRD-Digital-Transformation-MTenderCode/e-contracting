package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Contract @JsonCreator constructor(

        val token: String?,

        val id: String,

        var date: LocalDateTime?,

        val awardId: String,

        val status: ContractStatus,

        var statusDetails: ContractStatusDetails,

        var title: String?,

        var description: String?,

        val extendsContractID: String?,

        val budgetSource: List<BudgetSource>?,

        val classification: Classification?,

        var period: Period?,

        var value: ValueTax?,

        val items: List<Item>?,

        val dateSigned: LocalDateTime?,

        var documents: List<DocumentContract>?,

        val relatedProcesses: List<RelatedProcess>?,

        val amendments: List<Amendment>?,

        var milestones: List<Milestone>?,

        var confirmationRequests: List<ConfirmationRequest>?,

        var agreedMetrics: LinkedList<AgreedMetric>?
)