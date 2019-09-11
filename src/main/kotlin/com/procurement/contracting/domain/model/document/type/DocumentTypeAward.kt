package com.procurement.contracting.domain.model.document.type

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.exception.EnumException

enum class DocumentTypeAward(@JsonValue val value: String) {
    AWARD_NOTICE("awardNotice"),
    EVALUATION_REPORTS("evaluationReports"),
    SHORTLISTED_FIRMS("shortlistedFirms"),
    WINNING_BID("winningBid"),
    COMPLAINTS("complaints"),
    BIDDERS("bidders"),
    CONFLICT_OF_INTEREST("conflictOfInterest"),
    CANCELLATION_DETAILS("cancellationDetails"),
    CONTRACT_DRAFT("contractDraft"),
    CONTRACT_ARRANGEMENTS("contractArrangements"),
    CONTRACT_SCHEDULE("contractSchedule"),
    SUBMISSION_DOCUMENTS("submissionDocuments");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS: Map<String, DocumentTypeAward> = values().associateBy { it.value }

        fun fromString(value: String): DocumentTypeAward = CONSTANTS[value]
            ?: throw EnumException(
                enumType = DocumentTypeAward::class.java.name,
                value = value,
                values = values().toString()
            )
    }
}
