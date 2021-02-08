package com.procurement.contracting.infrastructure.web.dto.v2.request

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.request.SetStateForContractsRequest
import org.junit.jupiter.api.Test

class SetStateForContractsRequestTest : AbstractDTOTestBase<SetStateForContractsRequest>(SetStateForContractsRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/request/request_set_state_for_contracts_fully.json")
    }
}
