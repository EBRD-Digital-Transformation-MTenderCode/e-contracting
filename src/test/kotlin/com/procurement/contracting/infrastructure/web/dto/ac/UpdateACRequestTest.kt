package com.procurement.contracting.infrastructure.web.dto.ac

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v1.model.request.UpdateACRequest
import org.junit.jupiter.api.Test

class UpdateACRequestTest : AbstractDTOTestBase<UpdateACRequest>(UpdateACRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/web/dto/ac/request_update_ac_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/infrastructure/web/dto/ac/request_update_ac_required_1.json")
    }

    @Test
    fun required2() {
        testBindingAndMapping("json/infrastructure/web/dto/ac/request_update_ac_required_2.json")
    }

    @Test
    fun required3() {
        testBindingAndMapping("json/infrastructure/web/dto/ac/request_update_ac_required_3.json")
    }

}
