package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.fc.id.FrameworkContractId
import com.procurement.contracting.domain.model.parseCpid
import com.procurement.contracting.domain.model.parseOcid
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess

class CheckExistenceSupplierReferencesInFCParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val contracts: List<Contract>
) {
    companion object {

        fun tryCreate(
            cpid: String, ocid: String,
            contracts: List<Contract>
        ): Result<CheckExistenceSupplierReferencesInFCParams, DataErrors> {
            val parsedCpid = parseCpid(value = cpid)
                .onFailure { error -> return error }

            val parsedOcid = parseOcid(value = ocid)
                .onFailure { error -> return error }

            return CheckExistenceSupplierReferencesInFCParams(cpid = parsedCpid, ocid = parsedOcid, contracts = contracts).asSuccess()
        }
    }

    class Contract private constructor(
        val id: FrameworkContractId
    ) {
        companion object {
            fun tryCreate(id: String): Result<Contract, DataErrors.Validation.DataMismatchToPattern> {
                val contractId = FrameworkContractId.orNull(id)
                    ?: return DataErrors.Validation.DataMismatchToPattern(
                        name = "id", pattern = FrameworkContractId.pattern, actualValue = id
                    ).asFailure()

                return Contract(contractId).asSuccess()
            }
        }
    }
}