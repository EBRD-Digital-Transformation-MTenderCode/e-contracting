package com.procurement.contracting.infrastructure.dto.v2.response

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.response.GetCanByIdsResponse
import org.junit.jupiter.api.Test

class GetCanByIdsResponseTest : AbstractDTOTestBase<GetCanByIdsResponse>(GetCanByIdsResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/response/response_get_can_by_ids_fully.json")
    }

    @Test
    fun required_1() {
        testBindingAndMapping("json/dto/v2/response/response_get_can_by_ids_required_1.json")
    }

    @Test
    fun required_2() {
        testBindingAndMapping("json/dto/v2/response/response_get_can_by_ids_required_2.json")
    }
}
