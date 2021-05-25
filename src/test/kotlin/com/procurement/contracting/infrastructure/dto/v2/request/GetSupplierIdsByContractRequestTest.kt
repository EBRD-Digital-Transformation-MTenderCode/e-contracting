package com.procurement.contracting.infrastructure.dto.v2.request

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.request.GetSupplierIdsByContractRequest
import org.junit.jupiter.api.Test

class GetSupplierIdsByContractRequestTest : AbstractDTOTestBase<GetSupplierIdsByContractRequest>(GetSupplierIdsByContractRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/request/request_get_supplier_ids_by_contract_fully.json")
    }
}
