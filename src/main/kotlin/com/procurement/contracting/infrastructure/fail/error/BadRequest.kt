package com.procurement.contracting.infrastructure.fail.error

import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.infrastructure.fail.Fail

class BadRequest(override val description: String = "Invalid json", val exception: Exception) : Fail.Error() {

    override val code: String = "RQ-1"

    override fun logging(logger: Logger) {
        logger.error(message = message, exception = exception)
    }
}
