package com.procurement.contracting.application.service.treasury

import com.procurement.contracting.domain.model.treasury.TreasuryResponseStatus
import java.time.LocalDateTime

data class TreasuryProcessingData(
    val verification: Verification,
    val dateMet: LocalDateTime,
    val regData: RegData?
) {

    data class Verification(
            val status: TreasuryResponseStatus,
            val rationale: String
    )

    data class RegData(
            val externalRegId: String,
            val regDate: String
    )
}
