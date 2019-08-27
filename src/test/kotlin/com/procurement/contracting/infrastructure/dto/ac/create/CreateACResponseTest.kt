package com.procurement.contracting.infrastructure.dto.ac.create

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import org.junit.jupiter.api.Test

class CreateACResponseTest : AbstractDTOTestBase<CreateACResponse>(CreateACResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/ac/create/response/response_create_ac_fully.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/dto/ac/create/response/response_create_ac_required_1.json")
    }

    @Test
    fun required2() {
        testBindingAndMapping("json/dto/ac/create/response/response_create_ac_required_2.json")
    }
}
