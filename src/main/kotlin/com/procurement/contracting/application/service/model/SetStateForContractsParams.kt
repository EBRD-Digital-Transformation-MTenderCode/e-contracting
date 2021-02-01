package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.OperationType
import com.procurement.contracting.domain.model.ProcurementMethodDetails
import com.procurement.contracting.domain.model.parseCpid
import com.procurement.contracting.domain.model.parseEnum
import com.procurement.contracting.domain.model.parseOcid
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess

class SetStateForContractsParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val pmd: ProcurementMethodDetails,
    val country: String,
    val operationType: OperationType,
    val tender: Tender
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
                    OperationType.COMPLETE_SOURCING -> true
                    OperationType.WITHDRAW_QUALIFICATION_PROTOCOL -> false
                }
            }
            .toSet()

        fun tryCreate(
            cpid: String,
            ocid: String,
            pmd: String,
            country: String,
            operationType: String,
            tender: Tender
        ) : Result<SetStateForContractsParams, DataErrors.Validation> {
            val parsedCpid = parseCpid(value = cpid).onFailure { return it }
            val parsedOcid = parseOcid(value = ocid).onFailure { return it }

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

            return SetStateForContractsParams(
                cpid = parsedCpid,
                ocid = parsedOcid,
                pmd = parsedPmd,
                country = country,
                operationType = parsedOperationType,
                tender = tender
            ).asSuccess()

        }

    }

    data class Tender(
        val lots: List<Lot>
    ) {
        data class Lot(
            val id: String
        )
    }

}
