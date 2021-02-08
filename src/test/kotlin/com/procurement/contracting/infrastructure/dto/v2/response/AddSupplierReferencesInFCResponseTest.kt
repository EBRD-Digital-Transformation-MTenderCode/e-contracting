package com.procurement.contracting.infrastructure.dto.v2.response

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.response.AddSupplierReferencesInFCResponse
import org.junit.jupiter.api.Test

class AddSupplierReferencesInFCResponseTest : AbstractDTOTestBase<AddSupplierReferencesInFCResponse>(AddSupplierReferencesInFCResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/request/response_add_supplier_referefce_in_fc_fully.json")
    }
}
