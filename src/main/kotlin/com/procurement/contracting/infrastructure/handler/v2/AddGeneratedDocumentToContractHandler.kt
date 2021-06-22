package com.procurement.contracting.infrastructure.handler.v2

import com.procurement.contracting.application.service.FrameworkContractService
import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.application.service.Transform
import com.procurement.contracting.infrastructure.api.v2.CommandTypeV2
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.HistoryRepository
import com.procurement.contracting.infrastructure.handler.v2.base.AbstractHistoricalHandlerV2
import com.procurement.contracting.infrastructure.handler.v2.converter.convert
import com.procurement.contracting.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.contracting.infrastructure.handler.v2.model.request.AddGeneratedDocumentToContractRequest
import com.procurement.contracting.infrastructure.handler.v2.model.response.AddGeneratedDocumentToContractResponse
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.flatMap
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class AddGeneratedDocumentToContractHandler(
    private val frameworkContractService: FrameworkContractService,
    transform: Transform,
    @Qualifier("ocds") historyRepository: HistoryRepository,
    logger: Logger
) : AbstractHistoricalHandlerV2<AddGeneratedDocumentToContractResponse>(transform, historyRepository, logger) {

    override val action: CommandTypeV2 = CommandTypeV2.ADD_GENERATED_DOCUMENT_TO_CONTRACT

    override fun execute(descriptor: CommandDescriptor): Result<AddGeneratedDocumentToContractResponse, Fail> {
        val params = descriptor.body.asJsonNode
            .params<AddGeneratedDocumentToContractRequest>()
            .flatMap { it.convert() }
            .onFailure { return it }

        return frameworkContractService.addGeneratedDocumentToContract(params = params)
    }
}
