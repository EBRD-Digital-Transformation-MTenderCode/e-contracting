package com.procurement.contracting.infrastructure.dto.v2.request

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.request.ValidateConfirmationResponseDataRequest
import org.junit.jupiter.api.Test

class ValidateConfirmationResponseDataRequestTest : AbstractDTOTestBase<ValidateConfirmationResponseDataRequest>(ValidateConfirmationResponseDataRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/request/validate_confirmation_response_data_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/dto/v2/request/validate_confirmation_response_data_required_1.json")
    }

    @Test
    fun required2() {
        testBindingAndMapping("json/dto/v2/request/validate_confirmation_response_data_required_2.json")
    }
}
