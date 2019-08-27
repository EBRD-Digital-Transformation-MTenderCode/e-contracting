package com.procurement.contracting.infrastructure.dto.can.create

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import org.junit.jupiter.api.Test

class CreateCANRequestTest : AbstractDTOTestBase<CreateCANRequest>(CreateCANRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/can/create/request/request_create_can_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/dto/can/create/request/request_create_can_required_1.json")
    }
}
