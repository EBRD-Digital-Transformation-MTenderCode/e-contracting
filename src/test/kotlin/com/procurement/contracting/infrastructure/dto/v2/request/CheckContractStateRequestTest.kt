package com.procurement.contracting.infrastructure.dto.v2.request

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.request.CheckContractStateRequest
import org.junit.jupiter.api.Test

class CheckContractStateRequestTest : AbstractDTOTestBase<CheckContractStateRequest>(CheckContractStateRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/request/check_contract_state_request_full.json")
    }
}
