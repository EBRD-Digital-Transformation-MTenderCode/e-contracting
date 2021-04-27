package com.procurement.contracting.application.repository.confirmation

import com.procurement.contracting.application.service.Transform
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequest
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.domain.util.extension.toSetBy
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess

data class ConfirmationRequestEntity(
    val cpid: Cpid,
    val ocid: Ocid,
    val contractId: String,
    val id: ConfirmationRequestId,
    val groups: Set<String>,
    val jsonData: String
) {
    companion object {
        fun of(
            cpid: Cpid,
            ocid: Ocid,
            contractId: String,
            confirmationRequests: ConfirmationRequest,
            transform: Transform
        ): Result<ConfirmationRequestEntity, Fail> {
            val json = transform.trySerialization(confirmationRequests).onFailure { return it }
            return ConfirmationRequestEntity(
                cpid = cpid,
                ocid = ocid,
                contractId = contractId,
                id = confirmationRequests.id,
                groups = confirmationRequests.requestGroups.toSetBy { it.id },
                jsonData = json
            ).asSuccess()
        }
    }
}
