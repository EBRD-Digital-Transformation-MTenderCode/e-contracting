package com.procurement.contracting.infrastructure.dto.v2.response

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.response.GetSupplierIdsByContractResponse
import org.junit.jupiter.api.Test

class GetSupplierIdsByContractResponseTest : AbstractDTOTestBase<GetSupplierIdsByContractResponse>(GetSupplierIdsByContractResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/response/response_get_supplier_ids_by_contract_fully.json")
    }
}
