package com.procurement.contracting.infrastructure.dto.contract

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.request.CancelFrameworkContractRequest
import org.junit.jupiter.api.Test

class CancelFrameworkContractRequestTest : AbstractDTOTestBase<CancelFrameworkContractRequest>(
    CancelFrameworkContractRequest::class.java
) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/contract/request_cancel_framework_contract_fully.json")
    }
}
