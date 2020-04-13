package com.procurement.contracting.infrastructure.fail.error

import com.procurement.contracting.infrastructure.fail.Fail

sealed class ValidationError(
    numberError: String, override val description: String, val id: String? = null
) : Fail.Error("VR-") {
    override val code: String = prefix + numberError
}