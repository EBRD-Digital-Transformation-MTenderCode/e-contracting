package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.OperationType
import com.procurement.contracting.domain.model.parseCpid
import com.procurement.contracting.domain.model.parseEnum
import com.procurement.contracting.domain.model.parseOcid
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess

data class AddGeneratedDocumentToContractParams(
    val cpid: Cpid,
    val ocid: Ocid,
    val documentInitiator: OperationType,
    val contracts: List<Contract>
) {

    companion object {
        private val allowedOperationType = OperationType.allowedElements
            .filter {
                when (it) {
                    OperationType.ISSUING_FRAMEWORK_CONTRACT -> true

                    OperationType.WITHDRAW_QUALIFICATION_PROTOCOL,
                    OperationType.COMPLETE_SOURCING -> false
                }
            }
            .toSet()

        fun tryCreate(
            cpid: String,
            ocid: String,
            documentInitiator: String,
            contracts: List<Contract>
        ): Result<AddGeneratedDocumentToContractParams, DataErrors> {
            val parsedCpid = parseCpid(value = cpid).onFailure { return it }
            val parsedOcid = parseOcid(value = ocid).onFailure { return it }
            val parsedOperationType = parseEnum(
                value = documentInitiator,
                allowedEnums = allowedOperationType,
                attributeName = "documentInitiator",
                target = OperationType
            ).onFailure { return it }

            return AddGeneratedDocumentToContractParams(
                cpid = parsedCpid,
                ocid = parsedOcid,
                documentInitiator = parsedOperationType,
                contracts = contracts
            ).asSuccess()
        }
    }

    data class Contract(
        val documents: List<Document>
    ) {
        data class Document(
            val id: String,
        )
    }
}
