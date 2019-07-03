package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.contracting.domain.model.contract.status.ContractStatus
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails


data class ConfirmationCanRs @JsonCreator constructor(

        val cans: List<ConfirmationCan>,

        val lotId: String
)

data class ConfirmationCan @JsonCreator constructor(

    val id: String,

    var status: ContractStatus,

    var statusDetails: ContractStatusDetails
)