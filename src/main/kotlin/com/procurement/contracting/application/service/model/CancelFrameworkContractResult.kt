package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.fc.id.FrameworkContractId
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatus
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatusDetails

data class CancelFrameworkContractResult(
    val id: FrameworkContractId,
    val status: FrameworkContractStatus,
    val statusDetails: FrameworkContractStatusDetails
)
