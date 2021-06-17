package com.procurement.contracting.infrastructure.web.dto.v2.request

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.request.CreateContractRequest
import org.junit.jupiter.api.Test

class CreateContractRequestTest : AbstractDTOTestBase<CreateContractRequest>(CreateContractRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/request/create_contract_request_fully.json")
    }

    @Test
    fun required_1() {
        testBindingAndMapping("json/dto/v2/request/create_contract_request_required_1.json")
    }

    @Test
    fun required_2() {
        testBindingAndMapping("json/dto/v2/request/create_contract_request_required_2.json")
    }

    @Test
    fun required_3() {
        testBindingAndMapping("json/dto/v2/request/create_contract_request_required_3.json")
    }
}
