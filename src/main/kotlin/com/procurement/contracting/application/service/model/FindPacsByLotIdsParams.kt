package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.parseCpid
import com.procurement.contracting.domain.model.parseOcid
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.domain.util.extension.getDuplicate
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess

class FindPacsByLotIdsParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val tender: Tender,
) {
    companion object {

        fun tryCreate(cpid: String, ocid: String, tender: Tender): Result<FindPacsByLotIdsParams, DataErrors.Validation.DataMismatchToPattern> {
            val cpidParsed = parseCpid(value = cpid)
                .onFailure { error -> return error }

            val ocidParsed = parseOcid(value = ocid)
                .onFailure { error -> return error }

            return FindPacsByLotIdsParams(
                cpid = cpidParsed,
                ocid = ocidParsed,
                tender = tender
            ).asSuccess()
        }
    }

    class Tender private constructor(
        val lots: List<Lot>
    ) {
        companion object {

            fun tryCreate(lots: List<Lot>): Result<Tender, DataErrors.Validation.UniquenessDataMismatch> {

                val duplicate = lots.getDuplicate { it.id }
                if (duplicate != null)
                    return DataErrors.Validation.UniquenessDataMismatch(name = "lots.id", value = duplicate.id).asFailure()

                return Tender(lots = lots).asSuccess()
            }
        }

        data class Lot(
            val id: String
        )
    }


}