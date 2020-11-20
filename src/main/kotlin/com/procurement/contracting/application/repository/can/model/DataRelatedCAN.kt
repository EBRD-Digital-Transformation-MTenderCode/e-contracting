package com.procurement.contracting.application.repository.can.model

import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.can.status.CANStatusDetails

data class DataRelatedCAN(
    val id: CANId,
    val status: CANStatus,
    val statusDetails: CANStatusDetails,
    val jsonData: String
)
