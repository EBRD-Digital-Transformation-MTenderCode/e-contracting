package com.procurement.contracting.domain.model.ac.status

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class AwardContractStatus(@JsonValue override val key: String) : EnumElementProvider.Key {
    PENDING("pending"),
    ACTIVE("active"),
    CANCELLED("cancelled"),
    COMPLETE("complete"),
    TERMINATED("terminated"),
    UNSUCCESSFUL("unsuccessful");

    override fun toString(): String = key

    companion object : EnumElementProvider<AwardContractStatus>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = AwardContractStatus.orThrow(name)
    }
}
