package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.OperationType
import com.procurement.contracting.domain.model.fc.id.FrameworkContractId
import com.procurement.contracting.domain.model.parseCpid
import com.procurement.contracting.domain.model.parseEnum
import com.procurement.contracting.domain.model.parseOcid
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess

data class AddGeneratedDocumentToContractParams(
    val cpid: Cpid,
    val ocid: Ocid,
    val processInitiator: OperationType,
    val contracts: List<Contract>
) {

    companion object {
        private val allowedOperationType = OperationType.allowedElements
            .filter {
                when (it) {
                    OperationType.ISSUING_FRAMEWORK_CONTRACT -> true

                    OperationType.COMPLETE_SOURCING,
                    OperationType.CREATE_CONFIRMATION_RESPONSE_BY_BUYER,
                    OperationType.CREATE_CONFIRMATION_RESPONSE_BY_INVITED_CANDIDATE,
                    OperationType.WITHDRAW_QUALIFICATION_PROTOCOL -> false
                }
            }
            .toSet()

        fun tryCreate(
            cpid: String,
            ocid: String,
            processInitiator: String,
            contracts: List<Contract>
        ): Result<AddGeneratedDocumentToContractParams, DataErrors> {
            val parsedCpid = parseCpid(value = cpid).onFailure { return it }
            val parsedOcid = parseOcid(value = ocid).onFailure { return it }
            val parsedOperationType = parseEnum(
                value = processInitiator,
                allowedEnums = allowedOperationType,
                attributeName = "processInitiator",
                target = OperationType
            ).onFailure { return it }

            return AddGeneratedDocumentToContractParams(
                cpid = parsedCpid,
                ocid = parsedOcid,
                processInitiator = parsedOperationType,
                contracts = contracts
            ).asSuccess()
        }
    }

    data class Contract(
        val id: FrameworkContractId,
        val documents: List<Document>
    ) {
        companion object {
            fun tryCreate(id: String, documents: List<Document>): Result<Contract, DataErrors.Validation.DataMismatchToPattern> {
                val contractId = FrameworkContractId.orNull(id)
                    ?: return DataErrors.Validation.DataMismatchToPattern(
                        name = "id", pattern = FrameworkContractId.pattern, actualValue = id
                    ).asFailure()

                return Contract(id = contractId, documents = documents).asSuccess()
            }
        }

        data class Document(
            val id: String,
        )
    }
}
