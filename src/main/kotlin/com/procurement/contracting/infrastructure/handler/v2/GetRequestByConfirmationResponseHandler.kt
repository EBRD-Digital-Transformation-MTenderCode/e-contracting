package com.procurement.contracting.infrastructure.handler.v2

import com.procurement.contracting.application.service.ConfirmationRequestService
import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.infrastructure.api.v2.CommandTypeV2
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.v2.base.AbstractQueryHandlerV2
import com.procurement.contracting.infrastructure.handler.v2.converter.convert
import com.procurement.contracting.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.contracting.infrastructure.handler.v2.model.request.GetRequestByConfirmationResponseRequest
import com.procurement.contracting.infrastructure.handler.v2.model.response.GetRequestByConfirmationResponseResponse
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class GetRequestByConfirmationResponseHandler(
    private val confirmationRequestService: ConfirmationRequestService, logger: Logger
) : AbstractQueryHandlerV2<GetRequestByConfirmationResponseResponse>(logger) {

    override val action: CommandTypeV2 = CommandTypeV2.GET_REQUEST_BY_CONFIRMATION_RESPONSE

    override fun execute(descriptor: CommandDescriptor): Result<GetRequestByConfirmationResponseResponse, Fail> {
        val params = descriptor.body.asJsonNode
            .params<GetRequestByConfirmationResponseRequest>()
            .flatMap { it.convert() }
            .onFailure { return it }

        return confirmationRequestService.get(params = params)
    }
}