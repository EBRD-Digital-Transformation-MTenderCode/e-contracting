package com.procurement.contracting.domain.model.tender.status

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class TenderStatus(@JsonValue override val key: String) : EnumElementProvider.Key {
    PLANNING("planning"),
    PLANNED("planned"),
    ACTIVE("active"),
    CANCELLED("cancelled"),
    UNSUCCESSFUL("unsuccessful"),
    COMPLETE("complete"),
    WITHDRAWN("withdrawn");

    override fun toString(): String = key

    companion object : EnumElementProvider<TenderStatus>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = TenderStatus.orThrow(name)
    }
}
