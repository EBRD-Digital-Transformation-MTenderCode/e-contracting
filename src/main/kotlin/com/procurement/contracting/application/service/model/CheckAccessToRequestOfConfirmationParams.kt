package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestId
import com.procurement.contracting.domain.model.fc.id.FrameworkContractId
import com.procurement.contracting.domain.model.parseCpid
import com.procurement.contracting.domain.model.parseOcid
import com.procurement.contracting.domain.model.parseOwner
import com.procurement.contracting.domain.model.parseToken
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess

class CheckAccessToRequestOfConfirmationParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val token: Token,
    val owner: Owner,
    val contracts: List<Contract>,
) {
    companion object {

        fun tryCreate(
            cpid: String,
            ocid: String,
            token: String,
            owner: String,
            contracts: List<Contract>
        ): Result<CheckAccessToRequestOfConfirmationParams, DataErrors> {
            val parsedCpid = parseCpid(value = cpid).onFailure { return it }
            val parsedOcid = parseOcid(value = ocid).onFailure { return it }
            val parsedOwner = parseOwner(value = owner).onFailure { return it }
            val parsedToken = parseToken(value = token).onFailure { return it }

            return CheckAccessToRequestOfConfirmationParams(
                cpid = parsedCpid,
                ocid = parsedOcid,
                token = parsedToken,
                owner = parsedOwner,
                contracts = contracts
            ).asSuccess()
        }
    }

    data class Contract(
        val id: FrameworkContractId,
        val confirmationResponses: List<ConfirmationResponse>,
    ) {
        companion object {
            fun tryCreate(id: String, confirmationResponses: List<ConfirmationResponse>): Result<Contract, DataErrors> {
                val contractId = FrameworkContractId.orNull(id)
                    ?: return DataErrors.Validation.DataMismatchToPattern(
                        name = "id", pattern = FrameworkContractId.pattern, actualValue = id
                    ).asFailure()

                if (confirmationResponses.isEmpty())
                    return DataErrors.Validation.EmptyArray(name = "confirmationResponses").asFailure()

                return Contract(contractId, confirmationResponses).asSuccess()
            }
        }

        class ConfirmationResponse private constructor(
            val id: String,
            val requestId: ConfirmationRequestId
        ) {
            companion object {
                fun tryCreate(id: String, requestId: String): Result<ConfirmationResponse, DataErrors> {
                    val parsedRequestId = ConfirmationRequestId.orNull(requestId)
                        ?: return DataErrors.Validation.DataMismatchToPattern(
                            name = "requestId", pattern = ConfirmationRequestId.pattern, actualValue = id
                        ).asFailure()

                    return ConfirmationResponse(id, parsedRequestId).asSuccess()
                }
            }
        }
    }
}