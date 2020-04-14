package com.procurement.contracting.infrastructure.converter

import com.procurement.contracting.application.model.can.FindCANIdsParams
import com.procurement.contracting.domain.util.extension.mapResult
import com.procurement.contracting.infrastructure.dto.can.find.FindCANIdsRequest

fun FindCANIdsRequest.convert() =
    FindCANIdsParams.tryCreate(
        cpid = cpid,
        ocid = ocid,
        states = states?.mapResult { it.convert() }?.orForwardFail { error -> return error },
        lotIds = lotIds
    )

fun FindCANIdsRequest.State.convert() =
    FindCANIdsParams.State.tryCreate(status = status, statusDetails = statusDetails)