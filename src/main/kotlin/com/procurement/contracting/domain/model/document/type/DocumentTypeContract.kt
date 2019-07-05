package com.procurement.contracting.domain.model.document.type

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.exception.EnumException

enum class DocumentTypeContract(@JsonValue val value: String) {
    CONTRACT_NOTICE("contractNotice"),
    COMPLETION_CERTIFICATE("completionCertificate"),
    CONTRACT_DRAFT("contractDraft"),
    CONTRACT_ARRANGEMENTS("contractArrangements"),
    CONTRACT_SCHEDULE("contractSchedule"),
    ENVIRONMENTAL_IMPACT("environmentalImpact"),
    CONTRACT_ANNEXE("contractAnnexe"),
    CONTRACT_GUARANTEES("contractGuarantees"),
    SUB_CONTRACT("subContract"),
    ILLUSTRATION("illustration"),
    CONTRACT_SIGNED("contractSigned"),
    CONTRACT_SUMMARY("contractSummary"),
    CONFLICT_OF_INTEREST("conflictOfInterest"),
    CANCELLATION_DETAILS("cancellationDetails"),
    BUYERS_RESPONSE_ADD("buyersResponseAdd"),
    EVALUATION_REPORT("evaluationReports");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS: Map<String, DocumentTypeContract> = values().associateBy { it.value }

        fun fromString(value: String): DocumentTypeContract = CONSTANTS[value]
            ?: throw EnumException(
                enumType = DocumentTypeContract::class.java.name,
                value = value,
                values = values().toString()
            )
    }
}
