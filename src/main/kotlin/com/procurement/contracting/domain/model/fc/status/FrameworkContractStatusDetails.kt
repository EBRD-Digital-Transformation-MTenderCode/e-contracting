package com.procurement.contracting.domain.model.fc.status

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class FrameworkContractStatusDetails(@JsonValue override val key: String) : EnumElementProvider.Element {

    APPROVED("approved"),
    CONTRACT_PROJECT("contractProject"),
    ISSUED("issued"),
    ISSUING("issuing"),
    SIGNED("signed"),
    WITHDRAWN_QUALIFICATION_PROTOCOL("withdrawnQualificationProtocol"),
    ;

    override fun toString(): String = key

    companion object : EnumElementProvider<FrameworkContractStatusDetails>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = FrameworkContractStatusDetails.orThrow(name)
    }
}
