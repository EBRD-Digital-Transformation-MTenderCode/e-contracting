package com.procurement.contracting.infrastructure.dto.contract

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.request.CreateFrameworkContractRequest
import org.junit.jupiter.api.Test

class CreateFrameworkContractRequestTest : AbstractDTOTestBase<CreateFrameworkContractRequest>(CreateFrameworkContractRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/contract/request_create_framework_contract_fully.json")
    }
}
