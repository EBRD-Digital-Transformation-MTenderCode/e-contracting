package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.FindContractDocumentIdParams
import com.procurement.contracting.domain.model.parseCpid
import com.procurement.contracting.domain.model.parseOcid
import com.procurement.contracting.domain.model.parseProcessInitiator
import com.procurement.contracting.domain.model.process.ProcessInitiator
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.v2.converter.rule.notEmptyRule
import com.procurement.contracting.infrastructure.handler.v2.model.request.FindContractDocumentIdRequest
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess
import com.procurement.contracting.lib.functional.validate

private val allowedProcessInitiators = ProcessInitiator.allowedElements
    .filter {
        when (it) {
            ProcessInitiator.ISSUING_FRAMEWORK_CONTRACT,
            ProcessInitiator.NEXT_STEP_AFTER_BUYERS_CONFIRMATION -> true
        }
    }.toSet()

fun FindContractDocumentIdRequest.convert(): Result<FindContractDocumentIdParams, Fail> {
    contracts.validate(notEmptyRule("contracts"))
        .onFailure { return it }

    return FindContractDocumentIdParams(
        cpid = parseCpid(cpid).onFailure { return it },
        ocid = parseOcid(ocid).onFailure { return it },
        processInitiator = parseProcessInitiator(processInitiator, allowedProcessInitiators).onFailure { return it },
        contracts = contracts.map { FindContractDocumentIdParams.Contract(it.id) }
    ).asSuccess()
}
