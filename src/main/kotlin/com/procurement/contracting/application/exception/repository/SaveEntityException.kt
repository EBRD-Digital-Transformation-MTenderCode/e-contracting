package com.procurement.contracting.application.exception.repository

class SaveEntityException : RuntimeException {
    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(message: String) : super(message)
}
