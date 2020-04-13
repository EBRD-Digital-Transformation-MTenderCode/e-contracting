package com.procurement.contracting.domain.model.milestone.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class MilestoneType(@JsonValue override val key: String) : EnumElementProvider.Key {
    DELIVERY("delivery"),
    X_WARRANTY("x_warranty"),
    X_REPORTING("x_reporting"),
    APPROVAL("approval");

    override fun toString(): String = key

    companion object : EnumElementProvider<MilestoneType>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = MilestoneType.orThrow(name)
    }
}