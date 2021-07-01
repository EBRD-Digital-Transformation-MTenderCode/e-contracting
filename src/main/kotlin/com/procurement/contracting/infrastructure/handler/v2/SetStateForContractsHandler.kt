package com.procurement.contracting.infrastructure.handler.v2

import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.application.service.PacService
import com.procurement.contracting.application.service.Transform
import com.procurement.contracting.infrastructure.api.v2.CommandTypeV2
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.HistoryRepository
import com.procurement.contracting.infrastructure.handler.v2.base.AbstractHistoricalHandlerV2
import com.procurement.contracting.infrastructure.handler.v2.converter.convert
import com.procurement.contracting.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.contracting.infrastructure.handler.v2.model.request.SetStateForContractsRequest
import com.procurement.contracting.infrastructure.handler.v2.model.response.SetStateForContractsResponse
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.flatMap
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class SetStateForContractsHandler(
    private val pacService: PacService,
    transform: Transform,
    @Qualifier("ocds") historyRepository: HistoryRepository,
    logger: Logger
) : AbstractHistoricalHandlerV2<SetStateForContractsResponse>(transform, historyRepository, logger) {

    override val action: CommandTypeV2 = CommandTypeV2.SET_STATE_FOR_CONTRACTS

    override fun execute(descriptor: CommandDescriptor): Result<SetStateForContractsResponse, Fail> {
        val params = descriptor.body.asJsonNode
            .params<SetStateForContractsRequest>()
            .flatMap { it.convert() }
            .onFailure { return it }

        return pacService.setState(params = params)
    }
}
