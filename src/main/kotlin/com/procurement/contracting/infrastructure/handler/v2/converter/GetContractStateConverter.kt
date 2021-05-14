package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.GetContractStateParams
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.infrastructure.handler.v2.model.request.GetContractStateRequest
import com.procurement.contracting.lib.functional.Result

fun GetContractStateRequest.convert(): Result<GetContractStateParams, DataErrors> {
    val contracts = contracts.map { it.convert() }
    return GetContractStateParams.tryCreate(cpid = cpid, ocid = ocid, contracts = contracts)
}

fun GetContractStateRequest.Contract.convert() = GetContractStateParams.Contract(id = id)
