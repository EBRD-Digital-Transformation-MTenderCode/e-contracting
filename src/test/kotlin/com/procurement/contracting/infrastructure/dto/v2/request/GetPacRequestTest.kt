package com.procurement.contracting.infrastructure.dto.v2.request

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.request.GetPacRequest
import org.junit.jupiter.api.Test

class GetPacRequestTest : AbstractDTOTestBase<GetPacRequest>(GetPacRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/request/get_pac_request_full.json")
    }
}