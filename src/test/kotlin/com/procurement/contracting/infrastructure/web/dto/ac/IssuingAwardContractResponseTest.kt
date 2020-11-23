package com.procurement.contracting.infrastructure.web.dto.ac

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v1.model.response.IssuingAwardContractResponse
import org.junit.jupiter.api.Test

class IssuingAwardContractResponseTest : AbstractDTOTestBase<IssuingAwardContractResponse>(IssuingAwardContractResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/web/dto/ac/response_Issuing_ac_full.json")
    }

}
