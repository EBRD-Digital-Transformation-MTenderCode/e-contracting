package com.procurement.contracting.infrastructure.handler.v2

import com.procurement.contracting.application.service.FrameworkContractService
import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.infrastructure.api.v2.CommandTypeV2
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.v2.base.AbstractValidationHandlerV2
import com.procurement.contracting.infrastructure.handler.v2.converter.convert
import com.procurement.contracting.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.contracting.infrastructure.handler.v2.model.request.CheckContractStateRequest
import com.procurement.contracting.lib.functional.ValidationResult
import com.procurement.contracting.lib.functional.asValidationError
import com.procurement.contracting.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class CheckContractStateHandler(
    private val fsService: FrameworkContractService,
    logger: Logger
) : AbstractValidationHandlerV2(logger) {

    override val action: CommandTypeV2 = CommandTypeV2.CHECK_CONTRACT_STATE

    override fun execute(descriptor: CommandDescriptor): ValidationResult<Fail> {
        val params = descriptor.body.asJsonNode
            .params<CheckContractStateRequest>()
            .flatMap { it.convert() }
            .onFailure { return it.reason.asValidationError() }

        return fsService.checkContractState(params = params)
    }
}