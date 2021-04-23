package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.GetContractStateParams
import com.procurement.contracting.infrastructure.handler.v2.model.request.GetContractStateRequest

fun GetContractStateRequest.convert() = GetContractStateParams.tryCreate(cpid = cpid, ocid = ocid)