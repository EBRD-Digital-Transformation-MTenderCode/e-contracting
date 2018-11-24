package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.model.dto.ocds.*
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class FinalUpdateAcRq @JsonCreator constructor(
    val documents: List<Document>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Document @JsonCreator constructor(
    val id: String
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class FinalUpdateAcRs @JsonCreator constructor(

    val contract: ContractFinalUpdate,
    val approveBody: ApproveBody?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ContractFinalUpdate @JsonCreator constructor(

    val id: String,

    var date: LocalDateTime,

    val awardId: String,

    val status: ContractStatus,

    var statusDetails: ContractStatusDetails,

    var title: String?,

    var description: String?,

    var period: Period?,

    var documents: List<DocumentContract>?,

    var milestones: List<Milestone>?,

    var confirmationRequests: List<ConfirmationRequest>?,

    var value: ValueTax?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApproveBody @JsonCreator constructor(
    val name: String,
    val id: String,
    val identifier: Identifier,
    val contactPoint: ContactPoint
)


