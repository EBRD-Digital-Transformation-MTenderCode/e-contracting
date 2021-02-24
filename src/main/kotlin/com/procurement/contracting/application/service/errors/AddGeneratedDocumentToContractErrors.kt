package com.procurement.contracting.application.service.errors

import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.error.ValidationCommandError

sealed class AddGeneratedDocumentToContractErrors(
    override val description: String,
    numberError: String,
) : ValidationCommandError() {

    override val code: String = prefix + numberError

    class ContractNotFound(cpid: Cpid, ocid: Ocid) : AddGeneratedDocumentToContractErrors(
        numberError = "6.10.1",
        description = "Contract not found by cpid '$cpid' and ocid '$ocid'."
    )
}
