package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.FindCANIdsParams
import com.procurement.contracting.domain.util.extension.mapResult
import com.procurement.contracting.infrastructure.handler.v2.model.request.FindCANIdsRequest

fun FindCANIdsRequest.convert() =
    FindCANIdsParams.tryCreate(
        cpid = cpid,
        ocid = ocid,
        states = states?.mapResult { it.convert() }?.onFailure { error -> return error },
        lotIds = lotIds
    )

fun FindCANIdsRequest.State.convert() =
    FindCANIdsParams.State.tryCreate(status = status, statusDetails = statusDetails)