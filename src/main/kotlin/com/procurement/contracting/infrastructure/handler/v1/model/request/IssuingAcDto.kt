package com.procurement.contracting.infrastructure.handler.v1.model.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails
import java.time.LocalDateTime

data class IssuingAcRs @JsonCreator constructor(

        val contract: ContractIssuingAcRs
)


@JsonInclude(JsonInclude.Include.NON_NULL)
data class ContractIssuingAcRs @JsonCreator constructor(
        var date: LocalDateTime?,
        var statusDetails: ContractStatusDetails
)