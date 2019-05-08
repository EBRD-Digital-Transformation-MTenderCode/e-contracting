package com.procurement.contracting.exception


class ErrorException(val error: ErrorType, message: String? = null) :
    RuntimeException(
        if (message != null)
            "${error.message} $message"
        else
            error.message
    )
