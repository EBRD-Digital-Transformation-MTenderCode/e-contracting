package com.procurement.contracting.infrastructure.dto.v2.request

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.request.CheckAccessToRequestOfConfirmationRequest
import org.junit.jupiter.api.Test

class CheckAccessToRequestOfConfirmationRequestTest : AbstractDTOTestBase<CheckAccessToRequestOfConfirmationRequest>(CheckAccessToRequestOfConfirmationRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/request/check_access_to_request_of_confirmation_request_full.json")
    }
}
