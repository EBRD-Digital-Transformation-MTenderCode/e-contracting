package com.procurement.contracting.infrastructure.dto.v2.request

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.request.CheckExistenceSupplierReferencesInFCRequest
import org.junit.jupiter.api.Test

class CheckExistenceSupplierReferencesInFCRequestTest : AbstractDTOTestBase<CheckExistenceSupplierReferencesInFCRequest>(CheckExistenceSupplierReferencesInFCRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/request/check_existence_supplier_references_in_fc_full.json")
    }
}
