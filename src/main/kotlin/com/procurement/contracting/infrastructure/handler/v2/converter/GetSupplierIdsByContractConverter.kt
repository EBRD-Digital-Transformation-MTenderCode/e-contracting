package com.procurement.contracting.infrastructure.handler.v2.converter

import com.procurement.contracting.application.service.model.GetSupplierIdsByContractParams
import com.procurement.contracting.domain.model.parseCpid
import com.procurement.contracting.domain.model.parseOcid
import com.procurement.contracting.domain.model.parsePACId
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.infrastructure.handler.v2.converter.rule.notEmptyRule
import com.procurement.contracting.infrastructure.handler.v2.model.request.GetSupplierIdsByContractRequest
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess
import com.procurement.contracting.lib.functional.validate

fun GetSupplierIdsByContractRequest.convert(): Result<GetSupplierIdsByContractParams, DataErrors> {
    contracts.validate(notEmptyRule("contracts"))
        .onFailure { return it }

    return GetSupplierIdsByContractParams(
        cpid = parseCpid(cpid).onFailure { return it },
        ocid = parseOcid(ocid).onFailure { return it },
        contracts = contracts.map {
            GetSupplierIdsByContractParams.Contract(
                parsePACId(it.id, "contracts.id").onFailure { return it })
        }
    ).asSuccess()
}