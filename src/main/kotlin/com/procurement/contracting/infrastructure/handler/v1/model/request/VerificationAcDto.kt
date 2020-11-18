package com.procurement.contracting.infrastructure.handler.v1.model.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails

data class VerificationAcRs @JsonCreator constructor(

        val contract: ContractVerifiedAcRs
)


@JsonInclude(JsonInclude.Include.NON_NULL)
data class ContractVerifiedAcRs @JsonCreator constructor(

        var statusDetails: ContractStatusDetails
)