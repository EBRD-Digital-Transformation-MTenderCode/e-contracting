package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.CreateFrameworkContractParams
import com.procurement.contracting.domain.model.parseCpid
import com.procurement.contracting.domain.model.parseDate
import com.procurement.contracting.domain.model.parseOcid
import com.procurement.contracting.domain.model.parseOwner
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.infrastructure.handler.v2.model.request.CreateFrameworkContractRequest
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess

fun CreateFrameworkContractRequest.convert(): Result<CreateFrameworkContractParams, DataErrors> {
    val cpid = parseCpid(value = cpid).onFailure { return it }
    val ocid = parseOcid(value = ocid).onFailure { return it }
    val date = parseDate(value = date).onFailure { return it }
    val owner = parseOwner(value = owner).onFailure { return it }

    return CreateFrameworkContractParams(
        cpid = cpid,
        ocid = ocid,
        date = date,
        owner = owner
    ).asSuccess()
}
