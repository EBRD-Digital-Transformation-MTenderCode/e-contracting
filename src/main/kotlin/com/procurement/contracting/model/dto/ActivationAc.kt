package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.model.dto.ocds.ContractStatus
import com.procurement.contracting.model.dto.ocds.ContractStatusDetails
import com.procurement.contracting.model.dto.ocds.Milestone
import java.time.LocalDateTime

data class ActivationAcRs @JsonCreator constructor(

    val stageEnd:Boolean,
    val lotId:String,
        val contract: ContractActivationAcRs
)


@JsonInclude(JsonInclude.Include.NON_NULL)
data class ContractActivationAcRs @JsonCreator constructor(

    var status: ContractStatus,
    var statusDetails: ContractStatusDetails,
    val milestone: HashSet<Milestone>?
)