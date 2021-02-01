package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.fc.FrameworkContract
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid

data class AddSupplierReferencesInFCParams(
    val cpid: Cpid,
    val ocid: Ocid,
    val parties: List<Party>
) {
    data class Party(
        val id: String,
        val name: String
    ) {
        fun toDomain(): FrameworkContract.Supplier =
            FrameworkContract.Supplier(id = id, name = name)
    }
}
