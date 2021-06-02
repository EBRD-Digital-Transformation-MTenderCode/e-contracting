package com.procurement.contracting.application.service.errors

import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.error.ValidationCommandError

sealed class GetPacErrors(
    override val description: String,
    numberError: String,
) : ValidationCommandError() {

    override val code: String = prefix + numberError

    class UnexpectedIdentifiers : GetPacErrors(
        numberError = "6.21.1",
        description = "Request has more than 1 PAC identifier."
    )

    class PacNotFound(cpid: Cpid, ocid: Ocid, contractId: PacId) : GetPacErrors(
        numberError = "6.21.2",
        description = "PAC not found by cpid '$cpid', ocid '$ocid' and contract id '$contractId'."
    )
}
