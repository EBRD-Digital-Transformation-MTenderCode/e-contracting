package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.exception.EnumException


enum class MainProcurementCategory(@JsonValue val value: String) {
    GOODS("goods"),
    SERVICES("services"),
    WORKS("works");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS = HashMap<String, MainProcurementCategory>()

        init {
            MainProcurementCategory.values().forEach { CONSTANTS[it.value] = it }
        }

        fun fromValue(v: String): MainProcurementCategory {
            return CONSTANTS[v] ?: throw EnumException(MainProcurementCategory::class.java.name, v, values().toString())
        }
    }
}

enum class TransactionType(@JsonValue val value: String) {
    ADVANCE("advance"),
    PAYMENT("payment");


    override fun toString(): String {
        return this.value
    }
}

enum class MilestoneStatus(@JsonValue val value: String) {
    SCHEDULED("scheduled");

    override fun toString(): String {
        return this.value
    }
}

enum class ContractStatus(@JsonValue val value: String) {
    PENDING("pending"),
    ACTIVE("active"),
    CANCELLED("cancelled"),
    COMPLETE("complete"),
    TERMINATED("terminated"),
    UNSUCCESSFUL("unsuccessful");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS = HashMap<String, ContractStatus>()

        init {
            ContractStatus.values().forEach { CONSTANTS[it.value] = it }
        }

        fun fromValue(v: String): ContractStatus {
            return CONSTANTS[v] ?: throw EnumException(ContractStatus::class.java.name, v, values().toString())
        }
    }
}

enum class ContractStatusDetails(@JsonValue val value: String) {
    CONTRACT_PROJECT("contractProject"),
    CONTRACT_PREPARATION("contractPreparation"),
    ACTIVE("active"),
    VERIFIED("verified"),
    CANCELLED("cancelled"),
    COMPLETE("complete"),
    UNSUCCESSFUL("unsuccessful"),
    ISSUED("issued"),
    EMPTY("empty");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS = HashMap<String, ContractStatusDetails>()

        init {
            ContractStatusDetails.values().forEach { CONSTANTS[it.value] = it }
        }

        fun fromValue(v: String): ContractStatusDetails {
            return CONSTANTS[v] ?: throw EnumException(ContractStatusDetails::class.java.name, v, values().toString())
        }
    }
}

enum class TenderStatus(@JsonValue val value: String) {
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
}

enum class TenderStatusDetails(@JsonValue val value: String) {
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
}

//enum class DocumentType(@JsonValue val value: String) {
//    TENDER_NOTICE("tenderNotice"),
//    AWARD_NOTICE("awardNotice"),
//    CONTRACT_NOTICE("contractNotice"),
//    COMPLETION_CERTIFICATE("completionCertificate"),
//    PROCUREMENT_PLAN("procurementPlan"),
//    BIDDING_DOCUMENTS("biddingDocuments"),
//    TECHNICAL_SPECIFICATIONS("technicalSpecifications"),
//    EVALUATION_CRITERIA("evaluationCriteria"),
//    EVALUATION_REPORTS("evaluationReports"),
//    CONTRACT_DRAFT("contractDraft"),
//    CONTRACT_SIGNED("contractSigned"),
//    CONTRACT_ARRANGEMENTS("contractArrangements"),
//    CONTRACT_SCHEDULE("contractSchedule"),
//    PHYSICAL_PROGRESS_REPORT("physicalProgressReport"),
//    FINANCIAL_PROGRESS_REPORT("financialProgressReport"),
//    FINAL_AUDIT("finalAudit"),
//    HEARING_NOTICE("hearingNotice"),
//    MARKET_STUDIES("marketStudies"),
//    ELIGIBILITY_CRITERIA("eligibilityCriteria"),
//    CLARIFICATIONS("clarifications"),
//    SHORTLISTED_FIRMS("shortlistedFirms"),
//    ENVIRONMENTAL_IMPACT("environmentalImpact"),
//    ASSET_AND_LIABILITY_ASSESSMENT("assetAndLiabilityAssessment"),
//    RISK_PROVISIONS("riskProvisions"),
//    WINNING_BID("winningBid"),
//    COMPLAINTS("complaints"),
//    CONTRACT_ANNEXE("contractAnnexe"),
//    CONTRACT_GUARANTEES("contractGuarantees"),
//    SUB_CONTRACT("subContract"),
//    NEEDS_ASSESSMENT("needsAssessment"),
//    FEASIBILITY_STUDY("feasibilityStudy"),
//    PROJECT_PLAN("projectPlan"),
//    BILL_OF_QUANTITY("billOfQuantity"),
//    BIDDERS("bidders"),
//    CONFLICT_OF_INTEREST("conflictOfInterest"),
//    DEBARMENTS("debarments"),
//    ILLUSTRATION("illustration"),
//    SUBMISSION_DOCUMENTS("submissionDocuments"),
//    CONTRACT_SUMMARY("contractSummary"),
//    CANCELLATION_DETAILS("cancellationDetails");
//
//    override fun toString(): String {
//        return this.value
//    }
//}

enum class DocumentTypeAward(@JsonValue val value: String) {
    EVALUATION_REPORTS("evaluationReports"),
    AWARD_NOTICE("awardNotice"),
    WINNING_BID("winningBid");

    override fun toString(): String {
        return this.value
    }
}

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
    CONTRACT_SUMMARY("contractSummary");

    override fun toString(): String {
        return this.value
    }
}

enum class DocumentTypeBF(@JsonValue val value: String) {

    REGULATORY_DOCUMENT("regulatoryDocument");

    override fun toString(): String {
        return this.value
    }
}


enum class MilestoneType(@JsonValue val value: String) {

    DELIVERY("delivery"),
    X_WARRANTY("x_warranty"),
    X_REPORTING("x_reporting");


    override fun toString(): String {
        return this.value
    }
}


enum class RelatedProcessType(@JsonValue val value: String) {
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
}

enum class RelatedProcessScheme(@JsonValue val value: String) {
    OCID("ocid");

    override fun toString(): String {
        return this.value
    }
}