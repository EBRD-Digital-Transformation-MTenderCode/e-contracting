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
import com.procurement.contracting.infrastructure.handler.v2.model.request.CreatePacsRequest
import com.procurement.contracting.infrastructure.handler.v2.model.response.CreatePacsResponse
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class CreatePacsHandler(
    private val pacService: PacService,
    transform: Transform,
    historyRepository: HistoryRepository,
    logger: Logger
) : AbstractHistoricalHandlerV2<CreatePacsResponse>(transform, historyRepository, logger) {

    override val action: CommandTypeV2 = CommandTypeV2.CREATE_PACS

    override fun execute(descriptor: CommandDescriptor): Result<CreatePacsResponse, Fail> {
        val params = descriptor.body.asJsonNode
            .params<CreatePacsRequest>()
            .flatMap { it.convert() }
            .onFailure { return it }

        return pacService.create(params = params)
            .map { it.convert() }
    }
}
