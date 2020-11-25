package com.procurement.contracting.infrastructure.dto.can.create

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v1.model.request.CreateCANRequest
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
