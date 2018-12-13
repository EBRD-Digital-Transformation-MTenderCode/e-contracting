package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.model.dto.ocds.ContractStatusDetails
import java.time.LocalDateTime

data class IssuingAcRs @JsonCreator constructor(

        val contract: ContractIssuingAcRs
)


@JsonInclude(JsonInclude.Include.NON_NULL)
data class ContractIssuingAcRs @JsonCreator constructor(
        var date: LocalDateTime?,
        var statusDetails: ContractStatusDetails
)