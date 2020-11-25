package com.procurement.contracting.infrastructure.dto.can.cancel

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v1.model.request.CancelCANRequest
import org.junit.jupiter.api.Test

class CancelCANRequestTest : AbstractDTOTestBase<CancelCANRequest>(CancelCANRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/can/cancel/request/request_cancel_can_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/dto/can/cancel/request/request_cancel_can_required_1.json")
    }

    @Test
    fun required2() {
        testBindingAndMapping("json/dto/can/cancel/request/request_cancel_can_required_2.json")
    }
}
