package com.procurement.contracting.application.repository.fc.model

import com.procurement.contracting.application.service.Transform
import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.fc.FrameworkContract
import com.procurement.contracting.domain.model.fc.id.FrameworkContractId
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatus
import com.procurement.contracting.domain.model.fc.status.FrameworkContractStatusDetails
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess
import java.time.LocalDateTime

data class FrameworkContractEntity(
    val cpid: Cpid,
    val ocid: Ocid,
    val id: FrameworkContractId,
    val token: Token,
    val owner: Owner,
    val createdDate: LocalDateTime,
    val status: FrameworkContractStatus,
    val statusDetails: FrameworkContractStatusDetails,
    val jsonData: String
) {
    companion object {
        fun of(cpid: Cpid, ocid: Ocid, fc: FrameworkContract, transform: Transform): Result<FrameworkContractEntity, Fail> {
            val json = transform.trySerialization(fc).onFailure { return it }
            return FrameworkContractEntity(
                cpid = cpid,
                ocid = ocid,
                id = fc.id,
                token = fc.token,
                owner = fc.owner,
                createdDate = fc.date,
                status = fc.status,
                statusDetails = fc.statusDetails,
                jsonData = json
            ).asSuccess()
        }
    }
}
