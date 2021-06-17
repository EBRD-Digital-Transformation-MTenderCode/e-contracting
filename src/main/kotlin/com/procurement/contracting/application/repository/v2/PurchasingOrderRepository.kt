package com.procurement.contracting.application.repository.v2

import com.procurement.contracting.domain.model.po.PurchasingOrderId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.model.dto.ocds.v2.PurchasingOrder

interface PurchasingOrderRepository {
    fun findBy(cpid: Cpid, ocid: Ocid, id: PurchasingOrderId): Result<PurchasingOrderEntity?, Fail.Incident.Database>

    fun save(purchasingOrder: PurchasingOrder): Result<Boolean, Fail.Incident.Database>
}
