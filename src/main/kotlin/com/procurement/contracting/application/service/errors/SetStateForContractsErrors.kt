package com.procurement.contracting.application.service.errors

import com.procurement.contracting.infrastructure.fail.error.ValidationCommandError

sealed class SetStateForContractsErrors(
    override val description: String,
    numberError: String,
) : ValidationCommandError() {

    override val code: String = prefix + numberError

    class PacNotFound(relatedLot: String) : SetStateForContractsErrors(
        numberError = "6.6.1",
        description = "Cannot find pac with related lot $relatedLot."
    )
}
