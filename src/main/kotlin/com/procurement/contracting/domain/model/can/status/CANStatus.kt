package com.procurement.contracting.domain.model.can.status

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class CANStatus(@JsonValue override val key: String) : EnumElementProvider.Element {
    PENDING("pending"),
    ACTIVE("active"),
    CANCELLED("cancelled"),
    UNSUCCESSFUL("unsuccessful");

    override fun toString(): String = key

    companion object : EnumElementProvider<CANStatus>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = CANStatus.orThrow(name)
    }
}
