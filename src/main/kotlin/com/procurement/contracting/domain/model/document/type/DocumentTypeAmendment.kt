package com.procurement.contracting.domain.model.document.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class DocumentTypeAmendment(@JsonValue override val key: String) : EnumElementProvider.Key {
    CONTRACT_NOTICE("contractNotice"),
    CONTRACT_ARRANGEMENTS("contractArrangements"),
    CONTRACT_SCHEDULE("contractSchedule"),
    CONTRACT_ANNEXE("contractAnnexe"),
    CONTRACT_GUARANTEES("contractGuarantees"),
    SUB_CONTRACT("subContract"),
    ILLUSTRATION("illustration"),
    CONTRACT_SUMMARY("contractSummary"),
    CANCELLATION_DETAILS("cancellationDetails"),
    CONFLICT_OF_INTEREST("conflictOfInterest");

    override fun toString(): String = key

    companion object : EnumElementProvider<DocumentTypeAmendment>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = DocumentTypeAmendment.orThrow(name)
    }
}