package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Contract @JsonCreator constructor(

        val token: String?,

        val id: String,

        var date: LocalDateTime? = null,

        val awardId: String,

        var status: ContractStatus,

        var statusDetails: ContractStatusDetails,

        var title: String? = null,

        var description: String? = null,

        val extendsContractID: String? = null,

        val budgetSource: List<BudgetSource>? = null,

        val classification: Classification? = null,

        var period: Period? = null,

        var value: ValueTax? = null,

        val items: List<Item>? = null,

        val dateSigned: LocalDateTime? = null,

        var documents: List<DocumentContract>? = null,

        val relatedProcesses: List<RelatedProcess>? = null,

        val amendments: List<Amendment>? = null,

        var milestones: HashSet<Milestone>? = null,

        var confirmationRequests: HashSet<ConfirmationRequest>? = null,

        var confirmationResponses: HashSet<ConfirmationResponse>? = null,

        var agreedMetrics: LinkedList<AgreedMetric>? = null
)