package com.procurement.contracting.domain.model.lot

import com.procurement.contracting.domain.functional.Result
import com.procurement.contracting.domain.util.extension.tryUUID
import com.procurement.contracting.infrastructure.fail.Fail
import java.util.*

typealias LotId = UUID

fun String.tryLotId(): Result<LotId, Fail.Incident.Transform.Parsing> =
    this.tryUUID()
