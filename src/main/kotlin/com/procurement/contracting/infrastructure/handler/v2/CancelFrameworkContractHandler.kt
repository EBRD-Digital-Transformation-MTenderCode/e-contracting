package com.procurement.contracting.infrastructure.handler.v2

import com.procurement.contracting.application.service.CancelFrameworkContractService
import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.application.service.Transform
import com.procurement.contracting.infrastructure.api.v2.CommandTypeV2
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.HistoryRepository
import com.procurement.contracting.infrastructure.handler.v2.base.AbstractHistoricalHandlerV2
import com.procurement.contracting.infrastructure.handler.v2.converter.convert
import com.procurement.contracting.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.contracting.infrastructure.handler.v2.model.request.CancelFrameworkContractRequest
import com.procurement.contracting.infrastructure.handler.v2.model.response.CancelFrameworkContractResponse
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class CancelFrameworkContractHandler(
    private val cancelContractService: CancelFrameworkContractService,
    transform: Transform,
    historyRepository: HistoryRepository,
    logger: Logger
) : AbstractHistoricalHandlerV2<CancelFrameworkContractResponse>(transform, historyRepository, logger) {

    override val action: CommandTypeV2 = CommandTypeV2.CANCEL_FRAMEWORK_CONTRACT

    override fun execute(descriptor: CommandDescriptor): Result<CancelFrameworkContractResponse, Fail> {
        val params = descriptor.body.asJsonNode
            .params<CancelFrameworkContractRequest>()
            .flatMap { it.convert() }
            .onFailure { return it }

        return cancelContractService.cancel(params = params)
            .map { it.convert() }
    }
}
