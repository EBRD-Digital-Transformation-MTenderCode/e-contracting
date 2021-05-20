package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid

data class GetSupplierIdsByContractParams(
    val cpid: Cpid,
    val ocid: Ocid,
    val contracts: List<Contract>
) {
    data class Contract(
        val id: PacId
    )
}