package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.model.dto.ocds.Amendment
import com.procurement.contracting.model.dto.ocds.Can
import com.procurement.contracting.model.dto.ocds.ContractStatus
import com.procurement.contracting.model.dto.ocds.ContractStatusDetails

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CancelCanRq @JsonCreator constructor(

        val contract: CancelCanContract
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CancelCanContract @JsonCreator constructor(

        val amendment: Amendment
)


@JsonInclude(JsonInclude.Include.NON_NULL)
data class CancelCanRs @JsonCreator constructor(

        val can: Can,

        val acCancel: Boolean,

        val contract: CancelCanContractRs?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CancelCanContractRs @JsonCreator constructor(

        val id: String,

        var status: ContractStatus,

        var statusDetails: ContractStatusDetails
)
