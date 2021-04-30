package com.procurement.contracting.infrastructure.handler.v2

import com.procurement.contracting.application.service.ConfirmationResponseService
import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.infrastructure.api.v2.CommandTypeV2
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.v2.base.AbstractValidationHandlerV2
import com.procurement.contracting.infrastructure.handler.v2.converter.convert
import com.procurement.contracting.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.contracting.infrastructure.handler.v2.model.request.ValidateConfirmationResponseDataRequest
import com.procurement.contracting.lib.functional.ValidationResult
import com.procurement.contracting.lib.functional.asValidationError
import com.procurement.contracting.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class ValidateConfirmationResponseDataHandler(
    private val confirmationResponseService: ConfirmationResponseService,
    logger: Logger
) : AbstractValidationHandlerV2(logger) {

    override val action: CommandTypeV2 = CommandTypeV2.VALIDATE_CONFIRMATION_RESPONSE_DATA

    override fun execute(descriptor: CommandDescriptor): ValidationResult<Fail> {
        val params = descriptor.body.asJsonNode
            .params<ValidateConfirmationResponseDataRequest>()
            .flatMap { it.convert() }
            .onFailure { return it.reason.asValidationError() }

        return confirmationResponseService.validate(params = params)
    }
}