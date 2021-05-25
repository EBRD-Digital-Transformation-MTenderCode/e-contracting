package com.procurement.contracting.domain.model.document.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class DocumentTypeContract(@JsonValue override val key: String) : EnumElementProvider.Element {
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
    EVALUATION_REPORT("evaluationReports"),
    X_FRAMEWORK_CONTRACT("x_frameworkContract"),
    X_FRAMEWORK_PROJECT("x_frameworkProject"),
    ;

    override fun toString(): String = key

    companion object : EnumElementProvider<DocumentTypeContract>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = DocumentTypeContract.orThrow(name)
    }
}