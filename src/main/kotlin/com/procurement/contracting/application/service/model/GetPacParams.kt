package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.parseCpid
import com.procurement.contracting.domain.model.parseOcid
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess

data class GetPacParams(
    val cpid: Cpid,
    val ocid: Ocid,
    val contracts: List<Contract>
) {

    companion object {
        fun tryCreate(
            cpid: String,
            ocid: String,
            contracts: List<Contract>
        ): Result<GetPacParams, DataErrors> {
            val cpidParsed = parseCpid(value = cpid).onFailure { return it }
            val ocidParsed = parseOcid(value = ocid).onFailure { return it }

            if (contracts.isEmpty())
                return DataErrors.Validation.EmptyArray(name = "contracts").asFailure()

            return GetPacParams(
                cpid = cpidParsed,
                ocid = ocidParsed,
                contracts = contracts
            ).asSuccess()
        }
    }

    data class Contract(
        val id: PacId
    )
}