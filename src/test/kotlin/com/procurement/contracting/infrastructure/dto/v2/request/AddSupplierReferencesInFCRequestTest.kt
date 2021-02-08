package com.procurement.contracting.infrastructure.dto.v2.request

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.request.AddSupplierReferencesInFCRequest
import org.junit.jupiter.api.Test

class AddSupplierReferencesInFCRequestTest : AbstractDTOTestBase<AddSupplierReferencesInFCRequest>(AddSupplierReferencesInFCRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/request/request_add_supplier_referefce_in_fc_fully.json")
    }
}
