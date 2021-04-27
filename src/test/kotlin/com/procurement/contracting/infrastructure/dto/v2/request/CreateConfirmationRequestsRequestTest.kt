package com.procurement.contracting.infrastructure.dto.v2.request

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.request.CreateConfirmationRequestsRequest
import org.junit.jupiter.api.Test

class CreateConfirmationRequestsRequestTest : AbstractDTOTestBase<CreateConfirmationRequestsRequest>(CreateConfirmationRequestsRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/request/request_create_confirmation_requests_fully.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/dto/v2/request/request_create_confirmation_requests_required_1.json")
    }

    @Test
    fun required2() {
        testBindingAndMapping("json/dto/v2/request/request_create_confirmation_requests_required_2.json")
    }
}
