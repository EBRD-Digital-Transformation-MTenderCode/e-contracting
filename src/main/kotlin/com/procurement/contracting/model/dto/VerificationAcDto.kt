package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.model.dto.ocds.ContractStatusDetails

data class VerificationAcRs @JsonCreator constructor(

        val contract: ContractVerifiedAcRs
)


@JsonInclude(JsonInclude.Include.NON_NULL)
data class ContractVerifiedAcRs @JsonCreator constructor(

        var statusDetails: ContractStatusDetails
)