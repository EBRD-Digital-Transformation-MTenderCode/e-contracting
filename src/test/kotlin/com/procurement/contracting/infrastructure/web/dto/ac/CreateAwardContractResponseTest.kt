package com.procurement.contracting.infrastructure.web.dto.ac

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v1.model.response.CreateAwardContractResponse
import org.junit.jupiter.api.Test

class CreateAwardContractResponseTest : AbstractDTOTestBase<CreateAwardContractResponse>(CreateAwardContractResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/web/dto/ac/response_create_ac_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/infrastructure/web/dto/ac/response_create_ac_required_1.json")
    }

    @Test
    fun required2() {
        testBindingAndMapping("json/infrastructure/web/dto/ac/response_create_ac_required_2.json")
    }
}
