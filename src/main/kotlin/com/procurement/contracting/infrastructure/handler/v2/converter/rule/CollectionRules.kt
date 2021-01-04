package com.procurement.contracting.infrastructure.handler.v2.converter.rule

import com.procurement.contracting.domain.util.extension.getDuplicate
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.lib.functional.ValidationResult
import com.procurement.contracting.lib.functional.ValidationRule

fun <T : Collection<Any>?> noDuplicatesRule(attributeName: String): ValidationRule<T, DataErrors.Validation> =
    ValidationRule { received: T ->
        val duplicate = received?.getDuplicate { it }
        if (duplicate != null)
            ValidationResult.error(
                DataErrors.Validation.UniquenessDataMismatch(
                    value = duplicate.toString(), name = attributeName
                )
            )
        else
            ValidationResult.ok()
    }

fun <T : Collection<Any>?> notEmptyRule(attributeName: String): ValidationRule<T, DataErrors.Validation> =
    ValidationRule { received: T ->
        if (received != null && received.isEmpty())
            ValidationResult.error(DataErrors.Validation.EmptyArray(attributeName))
        else
            ValidationResult.ok()
    }