package com.procurement.contracting.domain.model.document.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class DocumentTypeAward(@JsonValue override val key: String) : EnumElementProvider.Key {
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

    override fun toString(): String = key

    companion object : EnumElementProvider<DocumentTypeAward>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = DocumentTypeAward.orThrow(name)
    }
}