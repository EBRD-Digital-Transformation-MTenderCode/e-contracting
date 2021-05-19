package com.procurement.contracting.infrastructure.dto.v2.request

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.request.CheckExistenceOfConfirmationResponsesRequest
import org.junit.jupiter.api.Test

class CheckExistenceOfConfirmationResponsesRequestTest : AbstractDTOTestBase<CheckExistenceOfConfirmationResponsesRequest>(
    CheckExistenceOfConfirmationResponsesRequest::class.java
) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/request/request_check_existence_of_confirmation_responses_full.json")
    }
}
