package com.procurement.contracting.domain.model

import com.procurement.contracting.domain.model.can.CAN
import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import org.junit.jupiter.api.Test

class CANTest : AbstractDTOTestBase<CAN>(CAN::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/domain/entity/can_entity_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/domain/entity/can_entity_required_1.json")
    }

    @Test
    fun required2() {
        testBindingAndMapping("json/domain/entity/can_entity_required_2.json")
    }

    @Test
    fun required3() {
        testBindingAndMapping("json/domain/entity/can_entity_required_3.json")
    }
}
