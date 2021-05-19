package com.procurement.contracting.domain.model.process

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class ProcessInitiator(@JsonValue override val key: String) : EnumElementProvider.Element {

    ISSUING_FRAMEWORK_CONTRACT("issuingFrameworkContract"),
    NEXT_STEP_AFTER_BUYERS_CONFIRMATION("nextStepAfterBuyersConfirmation"),
    ;

    override fun toString(): String = key

    companion object : EnumElementProvider<ProcessInitiator>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
