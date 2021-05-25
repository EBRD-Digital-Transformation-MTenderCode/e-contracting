package com.procurement.contracting.infrastructure.handler.v2.converter.extension

import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.lib.functional.ValidationResult
import com.procurement.contracting.lib.functional.asValidationError

fun String?.checkForBlank(path: String): ValidationResult<DataErrors.Validation.EmptyString> =
    if (this != null && this.isBlank())
        DataErrors.Validation.EmptyString(path).asValidationError()
    else
        ValidationResult.ok()