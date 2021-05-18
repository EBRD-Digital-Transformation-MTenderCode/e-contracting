package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid

data class CheckAccessToContractParams(
     val cpid: Cpid,
     val ocid: Ocid,
     val token: Token,
     val owner: Owner,
     val contracts: List<Contract>
) {
    data class Contract(
         val id: String
    )
}