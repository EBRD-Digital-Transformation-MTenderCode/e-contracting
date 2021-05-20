package com.procurement.contracting.application.service.errors

import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.domain.model.process.Stage
import com.procurement.contracting.infrastructure.fail.error.ValidationCommandError

sealed class GetSupplierIdsByContractErrors(
    override val description: String,
    numberError: String,
) : ValidationCommandError() {

    override val code: String = prefix + numberError

    class ContractNotFound(cpid: Cpid, ocid: Ocid, contractId: PacId) : GetSupplierIdsByContractErrors(
        numberError = "6.20.1",
        description = "Cannot find pac by cpid '$cpid', ocid '$ocid' and id '$contractId'."
    )

    class InvalidStage(stage: Stage) : GetSupplierIdsByContractErrors(
        numberError = "6.20.2",
        description = "Stage '$stage' is invalid."
    )
}
