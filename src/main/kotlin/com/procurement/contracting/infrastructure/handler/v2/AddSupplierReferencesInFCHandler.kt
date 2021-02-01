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
import com.procurement.contracting.infrastructure.handler.v2.model.request.AddSupplierReferencesInFCRequest
import com.procurement.contracting.infrastructure.handler.v2.model.response.AddSupplierReferencesInFCResponse
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class AddSupplierReferencesInFCHandler(
    private val frameworkContractService: FrameworkContractService,
    transform: Transform,
    historyRepository: HistoryRepository,
    logger: Logger
) : AbstractHistoricalHandlerV2<AddSupplierReferencesInFCResponse>(transform, historyRepository, logger) {

    override val action: CommandTypeV2 = CommandTypeV2.ADD_SUPPLIER_REFERENCES_IN_FC

    override fun execute(descriptor: CommandDescriptor): Result<AddSupplierReferencesInFCResponse, Fail> {
        val params = descriptor.body.asJsonNode
            .params<AddSupplierReferencesInFCRequest>()
            .flatMap { it.convert() }
            .onFailure { return it }

        return frameworkContractService.addSupplierReferences(params = params)
    }
}
