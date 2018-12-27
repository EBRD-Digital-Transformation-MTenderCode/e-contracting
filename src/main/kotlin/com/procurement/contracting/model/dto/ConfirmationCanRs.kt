package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.contracting.model.dto.ocds.ContractStatus
import com.procurement.contracting.model.dto.ocds.ContractStatusDetails


data class ConfirmationCanRs @JsonCreator constructor(

        val cans: List<ConfirmationCan>,

        val lotId: String
)

data class ConfirmationCan @JsonCreator constructor(

        val id: String,

        var status: ContractStatus,

        var statusDetails: ContractStatusDetails
)