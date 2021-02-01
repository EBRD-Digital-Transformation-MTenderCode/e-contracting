package com.procurement.contracting.infrastructure.web.dto.v2.response

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.response.SetStateForContractsResponse
import org.junit.jupiter.api.Test

class SetStateForContractsResponseTest : AbstractDTOTestBase<SetStateForContractsResponse>(SetStateForContractsResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/response/response_set_state_for_contracts_fully.json")
    }
}
