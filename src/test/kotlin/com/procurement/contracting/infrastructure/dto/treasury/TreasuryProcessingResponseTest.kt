package com.procurement.contracting.infrastructure.dto.treasury

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import org.junit.jupiter.api.Test

class TreasuryProcessingResponseTest :
    AbstractDTOTestBase<TreasuryProcessingResponse>(TreasuryProcessingResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/treasury/processing/response/response_treasury_processing_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/dto/treasury/processing/response/response_treasury_processing_required_1.json")
    }
}
