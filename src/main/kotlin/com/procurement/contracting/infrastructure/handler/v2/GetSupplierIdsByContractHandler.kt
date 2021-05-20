package com.procurement.contracting.infrastructure.handler.v2

import com.procurement.contracting.application.service.GetSupplierIdsByContractService
import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.infrastructure.api.v2.CommandTypeV2
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.handler.v2.base.AbstractQueryHandlerV2
import com.procurement.contracting.infrastructure.handler.v2.converter.convert
import com.procurement.contracting.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.contracting.infrastructure.handler.v2.model.request.GetSupplierIdsByContractRequest
import com.procurement.contracting.infrastructure.handler.v2.model.response.GetSupplierIdsByContractResponse
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class GetSupplierIdsByContractHandler(
    private val getSupplierIdsByContractService: GetSupplierIdsByContractService, logger: Logger
) : AbstractQueryHandlerV2<GetSupplierIdsByContractResponse>(logger) {

    override val action: CommandTypeV2 = CommandTypeV2.GET_SUPPLIER_IDS_BY_CONTRACT

    override fun execute(descriptor: CommandDescriptor): Result<GetSupplierIdsByContractResponse, Fail> {
        val params = descriptor.body.asJsonNode
            .params<GetSupplierIdsByContractRequest>()
            .flatMap { it.convert() }
            .onFailure { return it }

        return getSupplierIdsByContractService.get(params = params)
    }
}