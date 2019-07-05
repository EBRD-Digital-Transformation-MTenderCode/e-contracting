package com.procurement.contracting.application.service.treasury

import com.procurement.contracting.domain.model.treasury.TreasureResponseStatus
import java.time.LocalDateTime

data class TreasureProcessingData(
    val verification: Verification,
    val dateMet: LocalDateTime,
    val regData: RegData?
) {

    data class Verification(
        val status: TreasureResponseStatus,
        val rationale: String
    )

    data class RegData(
        val regNom: String,
        val regDate: String
    )
}
