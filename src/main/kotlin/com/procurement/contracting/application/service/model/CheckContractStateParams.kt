package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.OperationType
import com.procurement.contracting.domain.model.ProcurementMethodDetails
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

class CheckContractStateParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val pmd: ProcurementMethodDetails,
    val country: String,
    val operationType: OperationType,
    val contracts: List<Contract>,
) {
    companion object {

        private val allowedPmd = ProcurementMethodDetails.allowedElements
            .filter {
                when (it) {
                    ProcurementMethodDetails.CF, ProcurementMethodDetails.TEST_CF,
                    ProcurementMethodDetails.OF, ProcurementMethodDetails.TEST_OF -> true

                    ProcurementMethodDetails.CD, ProcurementMethodDetails.TEST_CD,
                    ProcurementMethodDetails.DA, ProcurementMethodDetails.TEST_DA,
                    ProcurementMethodDetails.DC, ProcurementMethodDetails.TEST_DC,
                    ProcurementMethodDetails.FA, ProcurementMethodDetails.TEST_FA,
                    ProcurementMethodDetails.GPA, ProcurementMethodDetails.TEST_GPA,
                    ProcurementMethodDetails.IP, ProcurementMethodDetails.TEST_IP,
                    ProcurementMethodDetails.MV, ProcurementMethodDetails.TEST_MV,
                    ProcurementMethodDetails.NP, ProcurementMethodDetails.TEST_NP,
                    ProcurementMethodDetails.OP, ProcurementMethodDetails.TEST_OP,
                    ProcurementMethodDetails.OT, ProcurementMethodDetails.TEST_OT,
                    ProcurementMethodDetails.RT, ProcurementMethodDetails.TEST_RT,
                    ProcurementMethodDetails.SV, ProcurementMethodDetails.TEST_SV -> false
                }
            }
            .toSet()

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
            pmd: String,
            country: String,
            operationType: String,
            contracts: List<Contract>
        ): Result<CheckContractStateParams, DataErrors> {
            val parsedCpid = parseCpid(value = cpid)
                .onFailure { error -> return error }

            val parsedOcid = parseOcid(value = ocid)
                .onFailure { error -> return error }

            val parsedPmd = parseEnum(
                value = pmd,
                allowedEnums = allowedPmd,
                attributeName = "pmd",
                target = ProcurementMethodDetails
            ).onFailure { return it }

            val parsedOperationType = parseEnum(
                value = operationType,
                allowedEnums = allowedOperationType,
                attributeName = "operationType",
                target = OperationType
            ).onFailure { return it }

            return CheckContractStateParams(
                cpid = parsedCpid,
                ocid = parsedOcid,
                pmd = parsedPmd,
                country = country,
                operationType = parsedOperationType,
                contracts = contracts
            ).asSuccess()
        }
    }

    data class Contract(
        val id: FrameworkContractId
    ) {
        companion object {
            fun tryCreate(id: String): Result<Contract, DataErrors> {
                val contractId = FrameworkContractId.orNull(id)
                    ?: return DataErrors.Validation.DataMismatchToPattern(
                        name = "id", pattern = FrameworkContractId.pattern, actualValue = id
                    ).asFailure()

                return Contract(contractId).asSuccess()
            }
        }
    }
}