package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.CancelFrameworkContractParams
import com.procurement.contracting.domain.model.OperationType
import com.procurement.contracting.domain.model.ProcurementMethodDetails
import com.procurement.contracting.domain.model.parseCpid
import com.procurement.contracting.domain.model.parseEnum
import com.procurement.contracting.domain.model.parseOcid
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.infrastructure.handler.v2.model.request.CancelFrameworkContractRequest
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess

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
            ProcurementMethodDetails.RFQ, ProcurementMethodDetails.TEST_RFQ,
            ProcurementMethodDetails.RT, ProcurementMethodDetails.TEST_RT,
            ProcurementMethodDetails.SV, ProcurementMethodDetails.TEST_SV -> false
        }
    }
    .toSet()

private val allowedOperationType = OperationType.allowedElements
    .filter {
        when (it) {
            OperationType.WITHDRAW_QUALIFICATION_PROTOCOL -> true

            OperationType.APPLY_CONFIRMATIONS,
            OperationType.COMPLETE_SOURCING,
            OperationType.CREATE_CONFIRMATION_RESPONSE_BY_BUYER,
            OperationType.CREATE_CONFIRMATION_RESPONSE_BY_INVITED_CANDIDATE,
            OperationType.CREATE_CONFIRMATION_RESPONSE_BY_SUPPLIER,
            OperationType.CREATE_CONTRACT,
            OperationType.ISSUING_FRAMEWORK_CONTRACT,
            OperationType.NEXT_STEP_AFTER_BUYERS_CONFIRMATION,
            OperationType.NEXT_STEP_AFTER_INVITED_CANDIDATES_CONFIRMATION,
            OperationType.NEXT_STEP_AFTER_SUPPLIERS_CONFIRMATION -> false
        }
    }
    .toSet()

fun CancelFrameworkContractRequest.convert(): Result<CancelFrameworkContractParams, DataErrors> {
    val cpid = parseCpid(value = cpid).onFailure { return it }
    val ocid = parseOcid(value = ocid).onFailure { return it }
    val pmd = parseEnum(
        value = pmd,
        allowedEnums = allowedPmd,
        attributeName = "pmd",
        target = ProcurementMethodDetails
    ).onFailure { return it }
    val operationType = parseEnum(
        value = operationType,
        allowedEnums = allowedOperationType,
        attributeName = "operationType",
        target = OperationType
    ).onFailure { return it }

    return CancelFrameworkContractParams(
        cpid = cpid,
        ocid = ocid,
        pmd = pmd,
        country = country,
        operationType = operationType
    ).asSuccess()
}
