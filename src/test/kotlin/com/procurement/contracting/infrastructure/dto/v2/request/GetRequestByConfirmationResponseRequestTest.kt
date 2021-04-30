package com.procurement.contracting.infrastructure.dto.v2.request

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.request.GetRequestByConfirmationResponseRequest
import org.junit.jupiter.api.Test

class GetRequestByConfirmationResponseRequestTest : AbstractDTOTestBase<GetRequestByConfirmationResponseRequest>(
    GetRequestByConfirmationResponseRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/request/get_request_by_confirmation_response_fully.json")
    }

}
