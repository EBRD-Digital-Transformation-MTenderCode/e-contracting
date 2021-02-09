package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.parseCpid
import com.procurement.contracting.domain.model.parseOcid
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess

class CheckExistenceSupplierReferencesInFCParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid
) {
    companion object {

        fun tryCreate(cpid: String, ocid: String): Result<CheckExistenceSupplierReferencesInFCParams, DataErrors> {
            val parsedCpid = parseCpid(value = cpid)
                .onFailure { error -> return error }

            val parsedOcid = parseOcid(value = ocid)
                .onFailure { error -> return error }

            return CheckExistenceSupplierReferencesInFCParams(cpid = parsedCpid, ocid = parsedOcid).asSuccess()
        }
    }
}