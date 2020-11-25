package com.procurement.contracting.infrastructure.web.dto.ac

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v1.model.request.FinalUpdateACRequest
import org.junit.jupiter.api.Test

class FinalUpdateRequestTest : AbstractDTOTestBase<FinalUpdateACRequest>(FinalUpdateACRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/web/dto/ac/request_final_update_ac_full.json")
    }

}
