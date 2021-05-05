package com.procurement.contracting.infrastructure.dto.v2.response

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.response.CreateConfirmationResponseResponse
import org.junit.jupiter.api.Test

class CreateConfirmationResponseResponseTest : AbstractDTOTestBase<CreateConfirmationResponseResponse>(CreateConfirmationResponseResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/response/create_confirmation_response_response_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/dto/v2/response/create_confirmation_response_response_required_1.json")
    }
}
