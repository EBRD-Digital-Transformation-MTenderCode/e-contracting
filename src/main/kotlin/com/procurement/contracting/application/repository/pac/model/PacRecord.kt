package com.procurement.contracting.application.repository.pac.model

import com.procurement.contracting.application.service.Transform
import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.fc.Pac
import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.pac.PacStatus
import com.procurement.contracting.domain.model.pac.PacStatusDetails
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess
import java.time.LocalDateTime

data class PacRecord(
    val cpid: Cpid,
    val ocid: Ocid,
    val id: PacId,
    val owner: Owner,
    val token: Token,
    val createdDate: LocalDateTime,
    val status: PacStatus,
    val statusDetails: PacStatusDetails?,
    val jsonData: String
) {
    companion object {
        fun of(cpid: Cpid, ocid: Ocid, pac: Pac, transform: Transform): Result<PacRecord, Fail> {
            val json = transform.trySerialization(pac.toEntity()).onFailure { return it }
            return PacRecord(
                cpid = cpid,
                ocid = ocid,
                id = pac.id,
                owner = pac.owner,
                token = pac.token,
                createdDate = pac.date,
                status = pac.status,
                statusDetails = pac.statusDetails,
                jsonData = json
            ).asSuccess()
        }
    }
}
