package com.procurement.contracting.infrastructure.dto.v2.response

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.response.CreateConfirmationRequestsResponse
import org.junit.jupiter.api.Test

class GetRequestByConfirmationResponseResponseTest : AbstractDTOTestBase<CreateConfirmationRequestsResponse>(CreateConfirmationRequestsResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/response/get_request_by_confirmation_params_fully.json")
    }
}
