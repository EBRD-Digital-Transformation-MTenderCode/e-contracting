package com.procurement.contracting.infrastructure.dto.can.cancel

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import org.junit.jupiter.api.Test

class CancelCANResponseTest : AbstractDTOTestBase<CancelCANResponse>(CancelCANResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/can/cancel/response/response_cancel_can_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/dto/can/cancel/response/response_cancel_can_required_1.json")
    }

    @Test
    fun required2() {
        testBindingAndMapping("json/dto/can/cancel/response/response_cancel_can_required_2.json")
    }

    @Test
    fun required3() {
        testBindingAndMapping("json/dto/can/cancel/response/response_cancel_can_required_3.json")
    }
}
