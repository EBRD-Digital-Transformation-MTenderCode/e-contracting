package com.procurement.contracting.infrastructure.handler.v2

import com.procurement.contracting.application.service.CreateContractService
import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.application.service.Transform
import com.procurement.contracting.infrastructure.api.v2.CommandTypeV2
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.HistoryRepository
import com.procurement.contracting.infrastructure.handler.v2.base.AbstractHistoricalHandlerV2
import com.procurement.contracting.infrastructure.handler.v2.converter.convert
import com.procurement.contracting.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.contracting.infrastructure.handler.v2.model.request.CreateContractRequest
import com.procurement.contracting.infrastructure.handler.v2.model.response.CreateContractResponse
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class CreateContractHandler(
    private val createContractService: CreateContractService,
    transform: Transform,
    historyRepository: HistoryRepository,
    logger: Logger
) : AbstractHistoricalHandlerV2<CreateContractResponse>(transform, historyRepository, logger) {

    override val action: CommandTypeV2 = CommandTypeV2.CREATE_CONTRACT

    override fun execute(descriptor: CommandDescriptor): Result<CreateContractResponse, Fail> {
        val params = descriptor.body.asJsonNode
            .params<CreateContractRequest>()
            .flatMap { it.convert() }
            .onFailure { return it }

        return createContractService.create(params = params)
    }
}
