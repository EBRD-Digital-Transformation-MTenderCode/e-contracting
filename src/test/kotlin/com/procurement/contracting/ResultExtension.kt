package com.procurement.contracting

import com.procurement.contracting.lib.functional.Result

fun <T, E> Result<T, E>.get(): T = when (this) {
    is Result.Success -> this.value
    is Result.Failure -> throw IllegalArgumentException("Result is not success.")
}

fun <T, E> Result<T, E>.assertFailure(): E = when (this) {
    is Result.Success -> throw IllegalArgumentException("Result is not failure.")
    is Result.Failure -> this.reason
}

fun <T, E> Result<T, E>.failure(): E = when (this) {
    is Result.Success -> throw IllegalArgumentException("Result is not failure.")
    is Result.Failure -> this.reason
}
