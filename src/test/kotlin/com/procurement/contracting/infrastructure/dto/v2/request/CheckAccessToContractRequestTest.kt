package com.procurement.contracting.infrastructure.dto.v2.request

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.request.CheckAccessToContractRequest
import org.junit.jupiter.api.Test

class CheckAccessToContractRequestTest : AbstractDTOTestBase<CheckAccessToContractRequest>(CheckAccessToContractRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/request/check_access_to_contract_request_full.json")
    }
}
