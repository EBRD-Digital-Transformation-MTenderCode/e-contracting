package com.procurement.contracting.infrastructure.web.dto.ac

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import org.junit.jupiter.api.Test

class FinalUpdateResponseTest : AbstractDTOTestBase<FinalUpdateACResponse>(FinalUpdateACResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/web/dto/ac/response_final_update_ac_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/infrastructure/web/dto/ac/response_final_update_ac_required_1.json")
    }
}
