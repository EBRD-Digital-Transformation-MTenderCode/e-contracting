package com.procurement.contracting.infrastructure.handler.v2

import com.procurement.contracting.application.service.ConfirmationRequestService
import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.application.service.Transform
import com.procurement.contracting.infrastructure.api.v2.CommandTypeV2
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.HistoryRepository
import com.procurement.contracting.infrastructure.handler.v2.base.AbstractHistoricalHandlerV2
import com.procurement.contracting.infrastructure.handler.v2.converter.convert
import com.procurement.contracting.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.contracting.infrastructure.handler.v2.model.request.CreateConfirmationRequestsRequest
import com.procurement.contracting.infrastructure.handler.v2.model.response.CreateConfirmationRequestsResponse
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class CreateConfirmationRequestsHandler(
    private val confirmationRequestService: ConfirmationRequestService,
    transform: Transform,
    historyRepository: HistoryRepository,
    logger: Logger
) : AbstractHistoricalHandlerV2<CreateConfirmationRequestsResponse>(transform, historyRepository, logger) {

    override val action: CommandTypeV2 = CommandTypeV2.CREATE_CONFIRMATION_REQUESTS

    override fun execute(descriptor: CommandDescriptor): Result<CreateConfirmationRequestsResponse, Fail> {
        val params = descriptor.body.asJsonNode
            .params<CreateConfirmationRequestsRequest>()
            .flatMap { it.convert() }
            .onFailure { return it }

        return confirmationRequestService.create(params = params)
    }
}
