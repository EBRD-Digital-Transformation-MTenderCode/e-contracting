package com.procurement.contracting.domain.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class ProcurementMethodDetails(@JsonValue override val key: String, val procurementMethod: String) :
    EnumElementProvider.Element {

    CD(key = "CD", procurementMethod = "selective"),
    CF(key = "CF", procurementMethod = "selective"),
    DA(key = "DA", procurementMethod = "limited"),
    DC(key = "DC", procurementMethod = "selective"),
    FA(key = "FA", procurementMethod = "limited"),
    GPA(key = "GPA", procurementMethod = "selective"),
    IP(key = "IP", procurementMethod = "selective"),
    MV(key = "MV", procurementMethod = "open"),
    NP(key = "NP", procurementMethod = "limited"),
    OF(key = "OF", procurementMethod = "selective"),
    OP(key = "OP", procurementMethod = "selective"),
    OT(key = "OT", procurementMethod = "open"),
    RT(key = "RT", procurementMethod = "selective"),
    SV(key = "SV", procurementMethod = "open"),
    TEST_CD(key = "TEST_CD", procurementMethod = "selective"),
    TEST_CF(key = "TEST_CF", procurementMethod = "selective"),
    TEST_DA(key = "TEST_DA", procurementMethod = "limited"),
    TEST_DC(key = "TEST_DC", procurementMethod = "selective"),
    TEST_FA(key = "TEST_FA", procurementMethod = "limited"),
    TEST_GPA(key = "TEST_GPA", procurementMethod = "selective"),
    TEST_IP(key = "TEST_IP", procurementMethod = "selective"),
    TEST_MV(key = "TEST_MV", procurementMethod = "open"),
    TEST_NP(key = "TEST_NP", procurementMethod = "limited"),
    TEST_OF(key = "TEST_OF", procurementMethod = "selective"),
    TEST_OP(key = "TEST_OP", procurementMethod = "selective"),
    TEST_OT(key = "TEST_OT", procurementMethod = "open"),
    TEST_RT(key = "TEST_RT", procurementMethod = "selective"),
    TEST_SV(key = "TEST_SV", procurementMethod = "open"),
    ;

    override fun toString(): String = key

    companion object : EnumElementProvider<ProcurementMethodDetails>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
