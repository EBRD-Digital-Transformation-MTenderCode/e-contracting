package com.procurement.contracting.infrastructure.dto.v2.request

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.request.GetCanByIdsRequest
import org.junit.jupiter.api.Test

class GetCanByIdsRequestTest : AbstractDTOTestBase<GetCanByIdsRequest>(GetCanByIdsRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/request/request_get_can_by_ids_fully.json")
    }
}
