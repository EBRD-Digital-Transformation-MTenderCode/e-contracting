package com.procurement.contracting.infrastructure.handler.v2

import com.procurement.contracting.application.service.ContractStateService
import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.infrastructure.api.v2.CommandTypeV2
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.v2.base.AbstractQueryHandlerV2
import com.procurement.contracting.infrastructure.handler.v2.converter.convert
import com.procurement.contracting.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.contracting.infrastructure.handler.v2.model.request.GetContractStateRequest
import com.procurement.contracting.infrastructure.handler.v2.model.response.GetContractStateResponse
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class GetContractStateHandler(
    private val contractStateService: ContractStateService, logger: Logger
) : AbstractQueryHandlerV2<GetContractStateResponse>(logger) {

    override val action: CommandTypeV2 = CommandTypeV2.GET_CONTRACT_STATE

    override fun execute(descriptor: CommandDescriptor): Result<GetContractStateResponse, Fail> {
        val params = descriptor.body.asJsonNode
            .params<GetContractStateRequest>()
            .flatMap { it.convert() }
            .onFailure { return it }

        return contractStateService.getState(params = params)
    }
}