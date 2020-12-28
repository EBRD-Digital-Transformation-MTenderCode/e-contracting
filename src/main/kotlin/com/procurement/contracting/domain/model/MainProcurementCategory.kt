package com.procurement.contracting.domain.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class MainProcurementCategory(@JsonValue override val key: String): EnumElementProvider.Element {
    GOODS("goods"),
    SERVICES("services"),
    WORKS("works");

    override fun toString(): String = key

    companion object : EnumElementProvider<MainProcurementCategory>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = MainProcurementCategory.orThrow(name)
    }
}
