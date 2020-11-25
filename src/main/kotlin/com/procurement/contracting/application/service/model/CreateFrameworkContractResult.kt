package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.fc.id.FrameworkContractId
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatus
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatusDetails
import java.time.LocalDateTime

data class CreateFrameworkContractResult(
    val id: FrameworkContractId,
    val token: Token,
    val status: FrameworkContractStatus,
    val statusDetails: FrameworkContractStatusDetails,
    val date: LocalDateTime,
    val isFrameworkOrDynamic: Boolean
)
