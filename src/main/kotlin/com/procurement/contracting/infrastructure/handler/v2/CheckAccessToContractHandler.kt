package com.procurement.contracting.infrastructure.handler.v2

import com.procurement.contracting.application.service.CheckAccessToContractService
import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.infrastructure.api.v2.CommandTypeV2
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.v2.base.AbstractValidationHandlerV2
import com.procurement.contracting.infrastructure.handler.v2.base.convert
import com.procurement.contracting.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.contracting.infrastructure.handler.v2.model.request.CheckAccessToContractRequest
import com.procurement.contracting.lib.functional.ValidationResult
import com.procurement.contracting.lib.functional.asValidationError
import com.procurement.contracting.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class CheckAccessToContractHandler(
    private val checkAccessToContractService: CheckAccessToContractService,
    logger: Logger
) : AbstractValidationHandlerV2(logger) {

    override val action: CommandTypeV2 = CommandTypeV2.CHECK_ACCESS_TO_CONTRACT

    override fun execute(descriptor: CommandDescriptor): ValidationResult<Fail> {
        val params = descriptor.body.asJsonNode
            .params<CheckAccessToContractRequest>()
            .flatMap { it.convert() }
            .onFailure { return it.reason.asValidationError() }

        return checkAccessToContractService.check(params = params)
    }
}