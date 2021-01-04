package com.procurement.contracting.domain.model.can.status

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class CANStatusDetails(@JsonValue override val key: String) : EnumElementProvider.Element {
    CONTRACT_PROJECT("contractProject"),
    ACTIVE("active"),
    UNSUCCESSFUL("unsuccessful"),
    EMPTY("empty"),
    TREASURY_REJECTION("treasuryRejection");

    override fun toString(): String = key

    companion object : EnumElementProvider<CANStatusDetails>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = CANStatusDetails.orThrow(name)
    }
}
