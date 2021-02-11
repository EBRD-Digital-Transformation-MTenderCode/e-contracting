package com.procurement.contracting.domain.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class OperationType(@JsonValue override val key: String) : EnumElementProvider.Element {

    COMPLETE_SOURCING("completeSourcing"),
    ISSUING_FRAMEWORK_CONTRACT("issuingFrameworkContract"),
    WITHDRAW_QUALIFICATION_PROTOCOL("withdrawQualificationProtocol")
    ;

    override fun toString(): String = key

    companion object : EnumElementProvider<OperationType>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
