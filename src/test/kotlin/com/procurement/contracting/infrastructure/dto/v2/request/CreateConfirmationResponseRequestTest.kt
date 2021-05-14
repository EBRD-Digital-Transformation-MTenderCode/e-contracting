package com.procurement.contracting.infrastructure.dto.v2.request

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.request.CreateConfirmationResponseRequest
import org.junit.jupiter.api.Test

class CreateConfirmationResponseRequestTest : AbstractDTOTestBase<CreateConfirmationResponseRequest>(CreateConfirmationResponseRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/request/create_confirmation_response_request_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/dto/v2/request/create_confirmation_response_request_required_1.json")
    }

    @Test
    fun required2() {
        testBindingAndMapping("json/dto/v2/request/create_confirmation_response_request_required_2.json")
    }
}
