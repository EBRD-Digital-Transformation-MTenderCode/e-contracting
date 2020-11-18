package com.procurement.contracting.application.repository.model

import com.procurement.contracting.domain.model.contract.status.ContractStatus
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails
import java.util.*

data class AcEntity(

    val cpId: String,

    var acId: String,

    val token: UUID,

    val owner: String,

    val createdDate: Date,

    var status: ContractStatus,

    var statusDetails: ContractStatusDetails,

    val mainProcurementCategory: String,

    val language: String,

    var jsonData: String
)
