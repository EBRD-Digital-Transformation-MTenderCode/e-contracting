package com.procurement.contracting.infrastructure.dto.v2.response

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.response.GetContractStateResponse
import org.junit.jupiter.api.Test

class GetContractStateResponseTest : AbstractDTOTestBase<GetContractStateResponse>(GetContractStateResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/request/get_contract_state_response_fully.json")
    }
}
