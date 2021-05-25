package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.OperationType
import com.procurement.contracting.domain.model.ProcurementMethodDetails
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid

data class CheckExistenceOfConfirmationResponsesParams(
    val cpid: Cpid,
    val ocid: Ocid,
    val pmd: ProcurementMethodDetails,
    val country: String,
    val operationType: OperationType,
    val contracts: List<Contract>
) {
    data class Contract(
        val id: String
    )
}