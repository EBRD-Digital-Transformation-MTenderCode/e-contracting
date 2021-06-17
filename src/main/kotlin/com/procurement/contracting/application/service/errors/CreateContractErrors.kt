package com.procurement.contracting.application.service.errors

import com.procurement.contracting.infrastructure.fail.error.ValidationCommandError

sealed class CreateContractErrors(
    override val description: String,
    numberError: String,
) : ValidationCommandError() {

    override val code: String = prefix + numberError

    class UnmatchingCurrency() : CreateContractErrors(
        numberError = "6.23.1",
        description = "Awards' values currencies do not match"
    )
}
