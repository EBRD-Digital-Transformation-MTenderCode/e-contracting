package com.procurement.contracting.application.service.errors

import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.error.ValidationCommandError

sealed class CheckExistenceSupplierReferencesInFCErrors(
    override val description: String,
    numberError: String,
) : ValidationCommandError() {

    override val code: String = prefix + numberError

    class ContractNotFound(cpid: Cpid, ocid: Ocid) : CheckExistenceSupplierReferencesInFCErrors(
        numberError = "6.9.1",
        description = "Contract not found by cpid '$cpid' and ocid '$ocid'."
    )

    class SuppliersNotFound() : CheckExistenceSupplierReferencesInFCErrors(
        numberError = "6.9.2",
        description = "The contract does not contain suppliers. At least one supplier must exist."
    )
}
