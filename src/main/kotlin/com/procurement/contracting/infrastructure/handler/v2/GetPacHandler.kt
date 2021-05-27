package com.procurement.contracting.infrastructure.handler.v2

import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.application.service.PacService
import com.procurement.contracting.infrastructure.api.v2.CommandTypeV2
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.v2.base.AbstractQueryHandlerV2
import com.procurement.contracting.infrastructure.handler.v2.converter.convert
import com.procurement.contracting.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.contracting.infrastructure.handler.v2.model.request.GetPacRequest
import com.procurement.contracting.infrastructure.handler.v2.model.response.GetPacResponse
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class GetPacHandler(
    private val pacService: PacService,
    logger: Logger
) : AbstractQueryHandlerV2<GetPacResponse>(logger) {

    override val action: CommandTypeV2 = CommandTypeV2.GET_PAC

    override fun execute(descriptor: CommandDescriptor): Result<GetPacResponse, Fail> {
        val params = descriptor.body.asJsonNode
            .params<GetPacRequest>()
            .flatMap { it.convert() }
            .onFailure { return it }

        return pacService.getPac(params)
    }
}