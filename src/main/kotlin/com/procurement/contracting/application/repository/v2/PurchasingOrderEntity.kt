package com.procurement.contracting.application.repository.v2

import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.po.status.PurchasingOrderStatus
import com.procurement.contracting.domain.model.po.status.PurchasingOrderStatusDetails
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.model.dto.ocds.v2.PurchasingOrder
import java.time.LocalDateTime

data class PurchasingOrderEntity(
    val cpid: Cpid,
    val ocid: Ocid,
    val token: Token,
    val owner: Owner,
    val createdDate: LocalDateTime,
    val status: PurchasingOrderStatus,
    val statusDetails: PurchasingOrderStatusDetails,
    val purchasingOrder: PurchasingOrder
)
