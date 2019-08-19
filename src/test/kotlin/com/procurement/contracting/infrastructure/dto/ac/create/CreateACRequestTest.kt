package com.procurement.contracting.infrastructure.dto.ac.create

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import org.junit.jupiter.api.Test

class CreateACRequestTest : AbstractDTOTestBase<CreateACRequest>(CreateACRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/ac/create/request/request_create_ac_fully.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/dto/ac/create/request/request_create_ac_required_1.json")
    }
}
