package com.procurement.contracting.infrastructure.dto.can.create

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v1.model.response.CreateCANResponse
import org.junit.jupiter.api.Test

class CreateCANResponseTest : AbstractDTOTestBase<CreateCANResponse>(CreateCANResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/can/create/response/response_create_can_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/dto/can/create/response/response_create_can_required_1.json")
    }
}
