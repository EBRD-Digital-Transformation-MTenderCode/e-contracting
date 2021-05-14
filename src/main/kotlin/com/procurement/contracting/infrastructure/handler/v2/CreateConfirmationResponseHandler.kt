package com.procurement.contracting.infrastructure.handler.v2

import com.procurement.contracting.application.service.ConfirmationResponseService
import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.application.service.Transform
import com.procurement.contracting.infrastructure.api.v2.CommandTypeV2
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.HistoryRepository
import com.procurement.contracting.infrastructure.handler.v2.base.AbstractHistoricalHandlerV2
import com.procurement.contracting.infrastructure.handler.v2.converter.convert
import com.procurement.contracting.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.contracting.infrastructure.handler.v2.model.request.CreateConfirmationResponseRequest
import com.procurement.contracting.infrastructure.handler.v2.model.response.CreateConfirmationResponseResponse
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class CreateConfirmationResponseHandler(
    private val confirmationResponseService: ConfirmationResponseService,
    transform: Transform,
    historyRepository: HistoryRepository,
    logger: Logger
) : AbstractHistoricalHandlerV2<CreateConfirmationResponseResponse>(transform, historyRepository, logger) {

    override val action: CommandTypeV2 = CommandTypeV2.CREATE_CONFIRMATION_RESPONSE

    override fun execute(descriptor: CommandDescriptor): Result<CreateConfirmationResponseResponse, Fail> {
        val params = descriptor.body.asJsonNode
            .params<CreateConfirmationResponseRequest>()
            .flatMap { it.convert() }
            .onFailure { return it }

        return confirmationResponseService.create(params = params)
    }
}
