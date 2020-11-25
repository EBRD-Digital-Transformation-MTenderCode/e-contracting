package com.procurement.contracting.domain.model.fc

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import org.junit.jupiter.api.Test

class FrameworkContractTest : AbstractDTOTestBase<FrameworkContract>(FrameworkContract::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/domain/entity/fc/framework_contract_entity_full.json")
    }
}
