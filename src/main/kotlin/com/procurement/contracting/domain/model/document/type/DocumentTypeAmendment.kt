package com.procurement.contracting.domain.model.document.type

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.exception.EnumException

enum class DocumentTypeAmendment(@JsonValue val value: String) {
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

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS: Map<String, DocumentTypeAmendment> = values().associateBy { it.value }

        fun fromString(value: String): DocumentTypeAmendment = CONSTANTS[value]
            ?: throw EnumException(
                enumType = DocumentTypeAmendment::class.java.name,
                value = value,
                values = values().toString()
            )
    }
}
