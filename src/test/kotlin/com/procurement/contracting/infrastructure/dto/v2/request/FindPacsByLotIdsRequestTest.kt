package com.procurement.contracting.infrastructure.dto.v2.request

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.request.FindPacsByLotIdsRequest
import org.junit.jupiter.api.Test

class FindPacsByLotIdsRequestTest : AbstractDTOTestBase<FindPacsByLotIdsRequest>(FindPacsByLotIdsRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/request/find_pacs_by_lot_ids_params_full.json")
    }
}
