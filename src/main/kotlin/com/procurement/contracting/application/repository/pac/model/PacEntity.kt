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

data class PacEntity(
    val cpid: Cpid,
    val ocid: Ocid,
    val id: PacId,
    val token: Token,
    val owner: Owner,
    val createdDate: LocalDateTime,
    val status: PacStatus,
    val statusDetails: PacStatusDetails,
    val jsonData: String
) {
    companion object {
        fun of(cpid: Cpid, ocid: Ocid, pac: Pac, transform: Transform): Result<PacEntity, Fail> {
            val json = transform.trySerialization(pac).onFailure { return it }
            return PacEntity(
                cpid = cpid,
                ocid = ocid,
                id = pac.id,
                token = pac.token,
                owner = pac.owner,
                createdDate = pac.date,
                status = pac.status,
                statusDetails = pac.statusDetails,
                jsonData = json
            ).asSuccess()
        }
    }
}
