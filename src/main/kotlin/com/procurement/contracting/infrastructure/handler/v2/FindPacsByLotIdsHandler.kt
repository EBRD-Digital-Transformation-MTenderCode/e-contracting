package com.procurement.contracting.infrastructure.handler.v2

import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.application.service.PacService
import com.procurement.contracting.infrastructure.api.v2.CommandTypeV2
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.v2.base.AbstractQueryHandlerV2
import com.procurement.contracting.infrastructure.handler.v2.converter.convert
import com.procurement.contracting.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.contracting.infrastructure.handler.v2.model.request.FindPacsByLotIdsRequest
import com.procurement.contracting.infrastructure.handler.v2.model.response.FindPacsByLotIdsResponse
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class FindPacsByLotIdsHandler(
    private val pacService: PacService,
    logger: Logger
) : AbstractQueryHandlerV2<FindPacsByLotIdsResponse>(logger) {

    override val action: CommandTypeV2 = CommandTypeV2.FIND_PACS_BY_LOT_IDS

    override fun execute(descriptor: CommandDescriptor): Result<FindPacsByLotIdsResponse, Fail> {
        val params = descriptor.body.asJsonNode
            .params<FindPacsByLotIdsRequest>()
            .flatMap { it.convert() }
            .onFailure { return it }

        return pacService.findPacsByLotIds(params = params)
            .map { it.convert() }
    }
}