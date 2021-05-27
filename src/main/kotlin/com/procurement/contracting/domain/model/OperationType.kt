package com.procurement.contracting.domain.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class OperationType(@JsonValue override val key: String) : EnumElementProvider.Element {

    APPLY_CONFIRMATIONS("applyConfirmations"),
    COMPLETE_SOURCING("completeSourcing"),
    CREATE_CONFIRMATION_RESPONSE_BY_BUYER("createConfirmationResponseByBuyer"),
    CREATE_CONFIRMATION_RESPONSE_BY_INVITED_CANDIDATE("createConfirmationResponseByInvitedCandidate"),
    CREATE_CONFIRMATION_RESPONSE_BY_SUPPLIER("createConfirmationResponseBySupplier"),
    ISSUING_FRAMEWORK_CONTRACT("issuingFrameworkContract"),
    NEXT_STEP_AFTER_BUYERS_CONFIRMATION("nextStepAfterBuyersConfirmation"),
    NEXT_STEP_AFTER_INVITED_CANDIDATES_CONFIRMATION("nextStepAfterInvitedCandidatesConfirmation"),
    NEXT_STEP_AFTER_SUPPLIERS_CONFIRMATION("nextStepAfterSuppliersConfirmation"),
    WITHDRAW_QUALIFICATION_PROTOCOL("withdrawQualificationProtocol")
    ;

    override fun toString(): String = key

    companion object : EnumElementProvider<OperationType>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
