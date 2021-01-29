package com.procurement.contracting.infrastructure.dto.v2.response

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.response.FindPacsByLotIdsResponse
import org.junit.jupiter.api.Test

class FindPacsByLotIdsResponseTest : AbstractDTOTestBase<FindPacsByLotIdsResponse>(FindPacsByLotIdsResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/response/find_pacs_by_lots_ids_result_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/dto/v2/response/find_pacs_by_lot_Ids_result_required_1.json")
    }

    @Test
    fun required2() {
        testBindingAndMapping("json/dto/v2/response/find_pacs_by_lot_Ids_result_required_2.json")
    }
}
