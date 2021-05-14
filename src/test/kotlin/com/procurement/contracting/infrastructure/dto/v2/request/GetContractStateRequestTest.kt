package com.procurement.contracting.infrastructure.dto.v2.request

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.request.GetContractStateRequest
import org.junit.jupiter.api.Test

class GetContractStateRequestTest : AbstractDTOTestBase<GetContractStateRequest>(GetContractStateRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/request/get_contract_state_params_full.json")
    }
}
