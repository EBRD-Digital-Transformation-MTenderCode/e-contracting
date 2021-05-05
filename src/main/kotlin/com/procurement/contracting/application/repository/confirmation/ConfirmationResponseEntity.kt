package com.procurement.contracting.application.repository.confirmation

import com.procurement.contracting.application.service.Transform
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestId
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationResponseId
import com.procurement.contracting.domain.model.confirmation.response.ConfirmationResponse
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess

data class ConfirmationResponseEntity(
    val cpid: Cpid,
    val ocid: Ocid,
    val contractId: String,
    val id: ConfirmationResponseId,
    val requestId: ConfirmationRequestId,
    val jsonData: String
) {
    companion object {
        fun of(
            cpid: Cpid,
            ocid: Ocid,
            contractId: String,
            confirmationResponse: ConfirmationResponse,
            transform: Transform
        ): Result<ConfirmationResponseEntity, Fail> {
            val json = transform.trySerialization(confirmationResponse).onFailure { return it }
            return ConfirmationResponseEntity(
                cpid = cpid,
                ocid = ocid,
                contractId = contractId,
                id = confirmationResponse.id,
                requestId = confirmationResponse.requestId,
                jsonData = json
            ).asSuccess()
        }
    }
}
