package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestId
import com.procurement.contracting.domain.model.parseCpid
import com.procurement.contracting.domain.model.parseOcid
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess

class GetRequestByConfirmationResponseParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val contracts: List<Contract>,
) {
    companion object {
        fun tryCreate(cpid: String, ocid: String, contracts: List<Contract>): Result<GetRequestByConfirmationResponseParams, DataErrors> {
            val cpidParsed = parseCpid(value = cpid).onFailure { return it }
            val ocidParsed = parseOcid(value = ocid).onFailure { return it }

            if (contracts.isEmpty())
                return DataErrors.Validation.EmptyArray(name = "contracts").asFailure()

            return GetRequestByConfirmationResponseParams(cpid = cpidParsed, ocid = ocidParsed, contracts = contracts).asSuccess()
        }
    }

    class Contract private constructor(
        val id: String,
        val confirmationResponses: List<ConfirmationResponse>
    ) {
        companion object {
            fun tryCreate(id: String, confirmationResponses: List<ConfirmationResponse>): Result<Contract, DataErrors> {
                if (confirmationResponses.isEmpty())
                    return DataErrors.Validation.EmptyArray(name = "confirmationResponses").asFailure()

                return Contract(id = id, confirmationResponses = confirmationResponses).asSuccess()
            }
        }

        class ConfirmationResponse private constructor(
            val id: String,
            val requestId: ConfirmationRequestId,
        ) {
            companion object {
                fun tryCreate(id: String, requestId: String): Result<ConfirmationResponse, DataErrors> {
                    val parsedRequestId = ConfirmationRequestId.orNull(requestId)
                        ?: return DataErrors.Validation.DataMismatchToPattern(
                            name = "requestId", pattern = ConfirmationRequestId.pattern, actualValue = requestId
                        ).asFailure()

                    return ConfirmationResponse(id = id, requestId = parsedRequestId).asSuccess()
                }
            }
        }
    }
}