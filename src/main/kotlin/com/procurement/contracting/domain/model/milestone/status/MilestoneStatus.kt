package com.procurement.contracting.domain.model.milestone.status

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class MilestoneStatus(@JsonValue override val key: String) : EnumElementProvider.Key {
    SCHEDULED("scheduled"),
    MET("met"),
    NOT_MET("notMet");

    override fun toString(): String = key

    companion object : EnumElementProvider<MilestoneStatus>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = MilestoneStatus.orThrow(name)
    }
}