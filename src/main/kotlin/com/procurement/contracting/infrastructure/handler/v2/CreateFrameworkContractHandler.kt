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
import com.procurement.contracting.infrastructure.handler.v2.model.request.CreateFrameworkContractRequest
import com.procurement.contracting.infrastructure.handler.v2.model.response.CreateFrameworkContractResponse
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.flatMap
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class CreateFrameworkContractHandler(
    private val frameworkContractService: FrameworkContractService,
    transform: Transform,
    @Qualifier("ocds") historyRepository: HistoryRepository,
    logger: Logger
) : AbstractHistoricalHandlerV2<CreateFrameworkContractResponse>(transform, historyRepository, logger) {

    override val action: CommandTypeV2 = CommandTypeV2.CREATE_FRAMEWORK_CONTRACT

    override fun execute(descriptor: CommandDescriptor): Result<CreateFrameworkContractResponse, Fail> {
        val params = descriptor.body.asJsonNode
            .params<CreateFrameworkContractRequest>()
            .flatMap { it.convert() }
            .onFailure { return it }

        return frameworkContractService.create(params = params)
            .map { it.convert() }
    }
}
