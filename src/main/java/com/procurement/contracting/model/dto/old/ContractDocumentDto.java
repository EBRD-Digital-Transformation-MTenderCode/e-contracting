package com.procurement.contracting.model.dto.old;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
        "documentType",
        "datePublished",
        "id",
        "url",
        "title",
        "description",
        "language"
})
public class ContractDocumentDto {
//
//    @JsonProperty("documentType")
//    @NotNull
//    @Valid
//    private final DocumentType documentType;
//
//    @JsonProperty("datePublished")
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @NotNull
//    private final LocalDateTime datePublished;
//
//    @JsonProperty("id")
//    @NotNull
//    private final String id;
//
//    @JsonProperty("url")
//    @NotNull
//    private final URI url;
//
//    @JsonProperty("title")
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    private final String title;
//
//    @JsonProperty("description")
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    private final String description;
//
//    @JsonProperty("language")
//    @NotNull
//    private final String language;
//
//    @JsonCreator
//    public ContractDocumentDto(@JsonProperty("id") final String id,
//                               @JsonProperty("documentType") final DocumentType documentType,
//                               @JsonProperty("title") final String title,
//                               @JsonProperty("description") final String description,
//                               @JsonProperty("url") final URI url,
//                               @JsonProperty("datePublished")
//                               @JsonDeserialize(using = LocalDateTimeDeserializer.class) final LocalDateTime
//                                       datePublished,
//                               @JsonProperty("language") final String language) {
//        this.id = id;
//        this.documentType = documentType;
//        this.title = title;
//        this.description = description;
//        this.url = url;
//        this.datePublished = datePublished;
//        this.language = language;
//    }
//
//    @Override
//    public int hashCode() {
//        return new HashCodeBuilder().append(id)
//                .append(documentType)
//                .append(title)
//                .append(description)
//                .append(url)
//                .append(datePublished)
//                .append(language)
//                .toHashCode();
//    }
//
//    @Override
//    public boolean equals(final Object other) {
//        if (other == this) {
//            return true;
//        }
//        if (!(other instanceof ContractDocumentDto)) {
//            return false;
//        }
//        final ContractDocumentDto rhs = (ContractDocumentDto) other;
//        return new EqualsBuilder().append(id, rhs.id)
//                .append(documentType, rhs.documentType)
//                .append(title, rhs.title)
//                .append(description, rhs.description)
//                .append(url, rhs.url)
//                .append(datePublished, rhs.datePublished)
//                .append(language, rhs.language)
//                .isEquals();
//    }
//
//    public enum DocumentType {
//        TENDER_NOTICE("tenderNotice"),
//        AWARD_NOTICE("awardNotice"),
//        CONTRACT_NOTICE("contractNotice"),
//        COMPLETION_CERTIFICATE("completionCertificate"),
//        PROCUREMENT_PLAN("procurementPlan"),
//        BIDDING_DOCUMENTS("biddingDocuments"),
//        TECHNICAL_SPECIFICATIONS("technicalSpecifications"),
//        EVALUATION_CRITERIA("evaluationCriteria"),
//        EVALUATION_REPORTS("evaluationReports"),
//        CONTRACT_DRAFT("contractDraft"),
//        CONTRACT_SIGNED("contractSigned"),
//        CONTRACT_ARRANGEMENTS("contractArrangements"),
//        CONTRACT_SCHEDULE("contractSchedule"),
//        PHYSICAL_PROGRESS_REPORT("physicalProgressReport"),
//        FINANCIAL_PROGRESS_REPORT("financialProgressReport"),
//        FINAL_AUDIT("finalAudit"),
//        HEARING_NOTICE("hearingNotice"),
//        MARKET_STUDIES("marketStudies"),
//        ELIGIBILITY_CRITERIA("eligibilityCriteria"),
//        CLARIFICATIONS("clarifications"),
//        SHORTLISTED_FIRMS("shortlistedFirms"),
//        ENVIRONMENTAL_IMPACT("environmentalImpact"),
//        ASSET_AND_LIABILITY_ASSESSMENT("assetAndLiabilityAssessment"),
//        RISK_PROVISIONS("riskProvisions"),
//        WINNING_BID("winningBid"),
//        COMPLAINTS("complaints"),
//        CONTRACT_ANNEXE("contractAnnexe"),
//        CONTRACT_GUARANTEES("contractGuarantees"),
//        SUB_CONTRACT("subContract"),
//        NEEDS_ASSESSMENT("needsAssessment"),
//        FEASIBILITY_STUDY("feasibilityStudy"),
//        PROJECT_PLAN("projectPlan"),
//        BILL_OF_QUANTITY("billOfQuantity"),
//        BIDDERS("bidders"),
//        CONFLICT_OF_INTEREST("conflictOfInterest"),
//        DEBARMENTS("debarments"),
//        ILLUSTRATION("illustration"),
//        SUBMISSION_DOCUMENTS("submissionDocuments"),
//        CONTRACT_SUMMARY("contractSummary"),
//        CANCELLATION_DETAILS("cancellationDetails");
//
//        private static final Map<String, DocumentType> CONSTANTS = new HashMap<>();
//
//        static {
//            for (final DocumentType c : values()) {
//                CONSTANTS.put(c.value, c);
//            }
//        }
//
//        private final String value;
//
//        DocumentType(final String value) {
//            this.value = value;
//        }
//
//        @JsonCreator
//        public static DocumentType fromValue(final String value) {
//            final DocumentType constant = CONSTANTS.get(value);
//            if (constant == null) {
//                throw new IllegalArgumentException(value);
//            }
//            return constant;
//        }
//
//        @Override
//        public String toString() {
//            return this.value;
//        }
//
//        @JsonValue
//        public String value() {
//            return this.value;
//        }
//    }
}
