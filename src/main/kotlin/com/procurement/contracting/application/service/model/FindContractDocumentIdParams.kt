package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.domain.model.process.ProcessInitiator

data class FindContractDocumentIdParams(
    val cpid: Cpid,
    val ocid: Ocid,
    val processInitiator: ProcessInitiator,
    val contracts: List<Contract>
) {
    data class Contract(
        val id: String
    )
}