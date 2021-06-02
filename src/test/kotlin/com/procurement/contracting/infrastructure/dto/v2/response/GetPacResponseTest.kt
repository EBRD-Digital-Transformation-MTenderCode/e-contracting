package com.procurement.contracting.infrastructure.dto.v2.response

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.response.GetPacResponse
import org.junit.jupiter.api.Test

class GetPacResponseTest : AbstractDTOTestBase<GetPacResponse>(GetPacResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/response/get_pac_response_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/dto/v2/response/get_pac_response_required_1.json")
    }

    @Test
    fun required2() {
        testBindingAndMapping("json/dto/v2/response/get_pac_response_required_2.json")
    }
}