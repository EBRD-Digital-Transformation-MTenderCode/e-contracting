package com.procurement.contracting.infrastructure.web.dto.v2.response

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.response.CreateContractResponse
import org.junit.jupiter.api.Test

class CreateContractResponseTest : AbstractDTOTestBase<CreateContractResponse>(CreateContractResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/response/create_contract_response_fully.json")
    }

    @Test
    fun required_1() {
        testBindingAndMapping("json/dto/v2/response/create_contract_response_required_1.json")
    }

    @Test
    fun required_2() {
        testBindingAndMapping("json/dto/v2/response/create_contract_response_required_2.json")
    }

    @Test
    fun required_3() {
        testBindingAndMapping("json/dto/v2/response/create_contract_response_required_3.json")
    }
}
