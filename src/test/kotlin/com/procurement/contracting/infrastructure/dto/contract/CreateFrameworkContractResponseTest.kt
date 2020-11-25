package com.procurement.contracting.infrastructure.dto.contract

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.response.CreateFrameworkContractResponse
import org.junit.jupiter.api.Test

class CreateFrameworkContractResponseTest : AbstractDTOTestBase<CreateFrameworkContractResponse>(CreateFrameworkContractResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/contract/response_create_framework_contract_fully.json")
    }
}
