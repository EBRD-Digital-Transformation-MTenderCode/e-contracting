package com.procurement.contracting.infrastructure.handler.v2

import com.procurement.contracting.application.service.CANService
import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.infrastructure.api.v2.CommandTypeV2
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.v2.base.AbstractQueryHandlerV2
import com.procurement.contracting.infrastructure.handler.v2.converter.convert
import com.procurement.contracting.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.contracting.infrastructure.handler.v2.model.request.FindCANIdsRequest
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class FindCANIdsHandler(
    private val CANService: CANService, logger: Logger
) : AbstractQueryHandlerV2<List<CANId>>(logger) {

    override val action: CommandTypeV2 = CommandTypeV2.FIND_CAN_IDS

    override fun execute(descriptor: CommandDescriptor): Result<List<CANId>, Fail> {
        val params = descriptor.body.asJsonNode
            .params<FindCANIdsRequest>()
            .flatMap { it.convert() }
            .onFailure { return it }

        return CANService.findCANIds(params = params)
    }
}