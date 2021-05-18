package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.CheckExistenceOfConfirmationResponsesParams
import com.procurement.contracting.domain.model.OperationType
import com.procurement.contracting.domain.model.ProcurementMethodDetails
import com.procurement.contracting.domain.model.parseCpid
import com.procurement.contracting.domain.model.parseOcid
import com.procurement.contracting.domain.model.parseOperationType
import com.procurement.contracting.domain.model.parsePmd
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.infrastructure.handler.v2.converter.rule.notEmptyRule
import com.procurement.contracting.infrastructure.handler.v2.model.request.CheckExistenceOfConfirmationResponsesRequest
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess
import com.procurement.contracting.lib.functional.validate

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
    }.toSet()

private val allowedOperationTypes = OperationType.allowedElements
    .filter {
        when (it) {
            OperationType.WITHDRAW_QUALIFICATION_PROTOCOL,
            OperationType.COMPLETE_SOURCING,
            OperationType.CREATE_CONFIRMATION_RESPONSE_BY_BUYER,
            OperationType.CREATE_CONFIRMATION_RESPONSE_BY_INVITED_CANDIDATE,
            OperationType.ISSUING_FRAMEWORK_CONTRACT -> false
        }
    }
    .toSet()

fun CheckExistenceOfConfirmationResponsesRequest.convert(): Result<CheckExistenceOfConfirmationResponsesParams, DataErrors> {
    contracts.validate(notEmptyRule("contracts"))
        .onFailure { return it }

    return CheckExistenceOfConfirmationResponsesParams(
        cpid = parseCpid(cpid).onFailure { return it },
        ocid = parseOcid(ocid).onFailure { return it },
        pmd = parsePmd(pmd, allowedPmd).onFailure { return it },
        country = country,
        operationType = parseOperationType(operationType, allowedOperationTypes).onFailure { return it }, //TODO()
        contracts = contracts.map { CheckExistenceOfConfirmationResponsesParams.Contract(it.id) }
    ).asSuccess()
}