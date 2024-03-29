package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.ac.id.AwardContractId
import com.procurement.contracting.domain.model.ac.status.AwardContractStatus
import com.procurement.contracting.domain.model.ac.status.AwardContractStatusDetails
import com.procurement.contracting.domain.model.award.AwardId
import java.time.LocalDateTime
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AwardContract @JsonCreator constructor(

    val token: Token?,

    val id: AwardContractId,

    var date: LocalDateTime? = null,

    val awardId: AwardId,

    var status: AwardContractStatus,

    var statusDetails: AwardContractStatusDetails,

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

    var milestones: MutableList<Milestone>? = null,

    var confirmationRequests: MutableList<ConfirmationRequest>? = null,

    var confirmationResponses: MutableList<ConfirmationResponse>? = null,

    var agreedMetrics: LinkedList<AgreedMetric>? = null,

    var internalId: String? = null
)
