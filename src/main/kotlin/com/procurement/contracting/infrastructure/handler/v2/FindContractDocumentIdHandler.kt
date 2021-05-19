package com.procurement.contracting.infrastructure.handler.v2

import com.procurement.contracting.application.service.FindContractDocumentIdService
import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.application.service.Transform
import com.procurement.contracting.infrastructure.api.v2.CommandTypeV2
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.HistoryRepository
import com.procurement.contracting.infrastructure.handler.v2.base.AbstractHistoricalHandlerV2
import com.procurement.contracting.infrastructure.handler.v2.converter.convert
import com.procurement.contracting.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.contracting.infrastructure.handler.v2.model.request.FindContractDocumentIdRequest
import com.procurement.contracting.infrastructure.handler.v2.model.response.FindContractDocumentIdResponse
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class FindContractDocumentIdHandler(
    private val findContractDocumentIdService: FindContractDocumentIdService,
    transform: Transform,
    historyRepository: HistoryRepository,
    logger: Logger
) : AbstractHistoricalHandlerV2<FindContractDocumentIdResponse?>(transform, historyRepository, logger) {

    override val action: CommandTypeV2 = CommandTypeV2.FIN_DCONTRACT_DOCUMENT_ID

    override fun execute(descriptor: CommandDescriptor): Result<FindContractDocumentIdResponse?, Fail> {
        val params = descriptor.body.asJsonNode
            .params<FindContractDocumentIdRequest>()
            .flatMap { it.convert() }
            .onFailure { return it }

        return findContractDocumentIdService.find(params = params)
    }
}
