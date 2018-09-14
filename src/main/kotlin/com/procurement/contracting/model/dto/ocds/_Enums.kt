package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.exception.EnumException
import java.util.*

enum class AwardStatus constructor(private val value: String) {
    PENDING("pending"),
    ACTIVE("active"),
    UNSUCCESSFUL("unsuccessful"),
    CONSIDERATION("consideration"),
    EMPTY("empty");

    override fun toString(): String {
        return this.value
    }

    @JsonValue
    fun value(): String {
        return this.value
    }

    companion object {

        private val CONSTANTS = HashMap<String, AwardStatus>()

        init {
            for (c in values()) {
                CONSTANTS[c.value] = c
            }
        }

        @JsonCreator
        fun fromValue(value: String): AwardStatus {
            return CONSTANTS[value] ?: throw IllegalArgumentException(value)
        }
    }
}

enum class ContractStatus constructor(private val value: String) {
    PENDING("pending"),
    ACTIVE("active"),
    CANCELLED("cancelled"),
    COMPLETE("complete"),
    TERMINATED("terminated"),
    UNSUCCESSFUL("unsuccessful");

    override fun toString(): String {
        return this.value
    }

    @JsonValue
    fun value(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS = HashMap<String, ContractStatus>()

        init {
            for (c in values()) {
                CONSTANTS[c.value] = c
            }
        }

        @JsonCreator
        fun fromValue(value: String): ContractStatus {
            return CONSTANTS[value] ?: throw IllegalArgumentException(value)
        }
    }
}

enum class ContractStatusDetails constructor(private val value: String) {
    CONTRACT_PROJECT("contractProject"),
    ACTIVE("active"),
    VERIFIED("verified"),
    CANCELLED("cancelled"),
    COMPLETE("complete"),
    UNSUCCESSFUL("unsuccessful"),
    EMPTY("empty");

    override fun toString(): String {
        return this.value
    }

    @JsonValue
    fun value(): String {
        return this.value
    }

    companion object {

        private val CONSTANTS = HashMap<String, ContractStatusDetails>()

        init {
            for (c in values()) {
                CONSTANTS[c.value] = c
            }
        }

        @JsonCreator
        fun fromValue(value: String): ContractStatusDetails {
            return CONSTANTS[value] ?: throw IllegalArgumentException(value)
        }
    }
}

enum class TenderStatus constructor(private val value: String) {
    PLANNING("planning"),
    PLANNED("planned"),
    ACTIVE("active"),
    CANCELLED("cancelled"),
    UNSUCCESSFUL("unsuccessful"),
    COMPLETE("complete"),
    WITHDRAWN("withdrawn");

    override fun toString(): String {
        return this.value
    }

    @JsonValue
    fun value(): String {
        return this.value
    }

    companion object {

        private val CONSTANTS = HashMap<String, TenderStatus>()

        init {
            for (c in values()) {
                CONSTANTS[c.value] = c
            }
        }

        @JsonCreator
        fun fromValue(value: String): TenderStatus {
            return CONSTANTS[value]
                    ?: throw EnumException(TenderStatus::class.java.name, value, Arrays.toString(values()))
        }
    }
}

enum class TenderStatusDetails constructor(private val value: String) {
    PRESELECTION("preselection"),
    PRESELECTED("preselected"),
    PREQUALIFICATION("prequalification"),
    PREQUALIFIED("prequalified"),
    EVALUATION("evaluation"),
    EVALUATED("evaluated"),
    EXECUTION("execution"),
    AWARDED("awarded"),
    //**//
    PLANNING("planning"),
    PLANNED("planned"),
    ACTIVE("active"),
    BLOCKED("blocked"),
    CANCELLED("cancelled"),
    UNSUCCESSFUL("unsuccessful"),
    COMPLETE("complete"),
    WITHDRAWN("withdrawn"),
    SUSPENDED("suspended"),
    EMPTY("empty");

    override fun toString(): String {
        return this.value
    }

    @JsonValue
    fun value(): String {
        return this.value
    }

    companion object {

        private val CONSTANTS = HashMap<String, TenderStatusDetails>()

        init {
            for (c in values()) {
                CONSTANTS[c.value] = c
            }
        }

        @JsonCreator
        fun fromValue(value: String): TenderStatusDetails {
            return CONSTANTS[value]
                    ?: throw EnumException(TenderStatusDetails::class.java.name, value, Arrays.toString(values()))
        }
    }
}

enum class DocumentType constructor(private val value: String) {

    TENDER_NOTICE("tenderNotice"),
    AWARD_NOTICE("awardNotice"),
    CONTRACT_NOTICE("contractNotice"),
    COMPLETION_CERTIFICATE("completionCertificate"),
    PROCUREMENT_PLAN("procurementPlan"),
    BIDDING_DOCUMENTS("biddingDocuments"),
    TECHNICAL_SPECIFICATIONS("technicalSpecifications"),
    EVALUATION_CRITERIA("evaluationCriteria"),
    EVALUATION_REPORTS("evaluationReports"),
    CONTRACT_DRAFT("contractDraft"),
    CONTRACT_SIGNED("contractSigned"),
    CONTRACT_ARRANGEMENTS("contractArrangements"),
    CONTRACT_SCHEDULE("contractSchedule"),
    PHYSICAL_PROGRESS_REPORT("physicalProgressReport"),
    FINANCIAL_PROGRESS_REPORT("financialProgressReport"),
    FINAL_AUDIT("finalAudit"),
    HEARING_NOTICE("hearingNotice"),
    MARKET_STUDIES("marketStudies"),
    ELIGIBILITY_CRITERIA("eligibilityCriteria"),
    CLARIFICATIONS("clarifications"),
    SHORTLISTED_FIRMS("shortlistedFirms"),
    ENVIRONMENTAL_IMPACT("environmentalImpact"),
    ASSET_AND_LIABILITY_ASSESSMENT("assetAndLiabilityAssessment"),
    RISK_PROVISIONS("riskProvisions"),
    WINNING_BID("winningBid"),
    COMPLAINTS("complaints"),
    CONTRACT_ANNEXE("contractAnnexe"),
    CONTRACT_GUARANTEES("contractGuarantees"),
    SUB_CONTRACT("subContract"),
    NEEDS_ASSESSMENT("needsAssessment"),
    FEASIBILITY_STUDY("feasibilityStudy"),
    PROJECT_PLAN("projectPlan"),
    BILL_OF_QUANTITY("billOfQuantity"),
    BIDDERS("bidders"),
    CONFLICT_OF_INTEREST("conflictOfInterest"),
    DEBARMENTS("debarments"),
    ILLUSTRATION("illustration"),
    SUBMISSION_DOCUMENTS("submissionDocuments"),
    CONTRACT_SUMMARY("contractSummary"),
    CANCELLATION_DETAILS("cancellationDetails");

    override fun toString(): String {
        return this.value
    }

    @JsonValue
    fun value(): String {
        return this.value
    }

    companion object {

        private val CONSTANTS = HashMap<String, DocumentType>()

        init {
            for (c in values()) {
                CONSTANTS[c.value] = c
            }
        }

        @JsonCreator
        fun fromValue(value: String): DocumentType {
            return CONSTANTS[value]
                    ?: throw EnumException(DocumentType::class.java.name, value, Arrays.toString(values()))
        }
    }
}

enum class RelatedProcessType constructor(private val value: String) {
    FRAMEWORK("framework"),
    PLANNING("planning"),
    PARENT("parent"),
    PRIOR("prior"),
    UNSUCCESSFUL_PROCESS("unsuccessfulProcess"),
    SUB_CONTRACT("subContract"),
    REPLACEMENT_PROCESS("replacementProcess"),
    RENEWAL_PROCESS("renewalProcess");

    override fun toString(): String {
        return this.value
    }

    @JsonValue
    fun value(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS = HashMap<String, RelatedProcessType>()

        init {
            for (c in values()) {
                CONSTANTS[c.value] = c
            }
        }

        @JsonCreator
        fun fromValue(value: String): RelatedProcessType {
            return CONSTANTS[value] ?: throw IllegalArgumentException(value)
        }
    }
}

enum class RelatedProcessScheme constructor(private val value: String) {
    OCID("ocid");

    override fun toString(): String {
        return this.value
    }

    @JsonValue
    fun value(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS = HashMap<String, RelatedProcessScheme>()

        init {
            for (c in values()) {
                CONSTANTS[c.value] = c
            }
        }

        @JsonCreator
        fun fromValue(value: String): RelatedProcessScheme {
            return CONSTANTS[value] ?: throw IllegalArgumentException(value)
        }
    }
}