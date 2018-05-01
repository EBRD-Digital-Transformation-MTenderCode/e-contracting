package com.procurement.contracting.model.dto.old.updateAC;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
        "id",
        "budgetSource",
        "title",
        "description",
        "statusDetails",
        "classification",
        "period",
        "value",
        "items",
        "dateSigned",
        "documents",
        "relatedProcesses",
        "amendments"
})
public class UpdateACContractRQDto {
//    @JsonProperty("id")
//    @NotNull
//    private final String id;
//
//    @JsonProperty("budgetSource")
//    @NotEmpty
//    @Valid
//    private final List<ContractBudgetSourceDto> budgetSource;
//
//    @JsonProperty("title")
//    @NotNull
//    private final String title;
//
//    @JsonProperty("description")
//    @NotNull
//    private final String description;
//
//    @JsonProperty("statusDetails")
//    @NotNull
//    @Valid
//    private final ContractStatusDetails statusDetails;
//
//    @JsonProperty("classification")
//    @NotNull
//    @Valid
//    private final ClassificationDto classification;
//
//    @JsonProperty("period")
//    @Valid
//    @NotNull
//    private final ContractPeriodDto period;
//
//    @JsonProperty("dateSigned")
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    private final LocalDateTime dateSigned;
//
//    @JsonProperty("documents")
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    @Valid
//    private final List<ContractDocumentDto> documents;
//
//    @JsonProperty("relatedProcesses")
//    @JsonDeserialize(as = LinkedHashSet.class)
//    @NotNull
//    @Valid
//    private final Set<UpdateACRelatedProcessDto> relatedProcesses;
//
//    @JsonProperty("amendments")
//    @NotNull
//    @Valid
//    private final List<AmendmentDto> amendments;
//
//    public UpdateACContractRQDto(@JsonProperty("id")
//                                 @NotNull final String id,
//                                 @JsonProperty("budgetSource")
//                                 @NotEmpty
//                                 @Valid final List<ContractBudgetSourceDto> budgetSource,
//                                 @JsonProperty("title")
//                                 @NotNull final String title,
//                                 @JsonProperty("description")
//                                 @NotNull final String description,
//                                 @JsonProperty("statusDetails")
//                                 @NotNull
//                                 @Valid final ContractStatusDetails statusDetails,
//                                 @JsonProperty("classification")
//                                 @NotNull
//                                 @Valid final ClassificationDto classification,
//                                 @JsonProperty("period")
//                                 @Valid
//                                 @NotNull final ContractPeriodDto period,
//                                 @JsonProperty("dateSigned")
//                                 @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//                                 @JsonInclude(JsonInclude.Include.NON_NULL) final LocalDateTime dateSigned,
//                                 @JsonProperty("documents")
//                                 @JsonInclude(JsonInclude.Include.NON_NULL)
//                                 @Valid final List<ContractDocumentDto> documents,
//                                 @JsonProperty("relatedProcesses")
//                                 @NotNull
//                                 @Valid final LinkedHashSet<UpdateACRelatedProcessDto> relatedProcesses,
//                                 @JsonProperty("amendments")
//                                 @NotNull
//                                 @Valid final List<AmendmentDto> amendments) {
//        this.id = id;
//        this.budgetSource = budgetSource;
//        this.title = title;
//        this.description = description;
//        this.statusDetails = statusDetails;
//        this.classification = classification;
//        this.period = period;
//        this.dateSigned = dateSigned;
//        this.documents = documents;
//        this.relatedProcesses = relatedProcesses;
//        this.amendments = amendments;
//    }
//
//    @Override
//    public int hashCode() {
//        return new HashCodeBuilder().append(id)
//                                    .append(title)
//                                    .append(description)
//                                    .append(statusDetails)
//                                    .append(classification)
//                                    .append(period)
//                                    .append(dateSigned)
//                                    .append(documents)
//                                    .append(relatedProcesses)
//                                    .append(amendments)
//                                    .toHashCode();
//    }
//
//    @Override
//    public boolean equals(final Object other) {
//        if (other == this) {
//            return true;
//        }
//        if (!(other instanceof UpdateACContractRQDto)) {
//            return false;
//        }
//        final UpdateACContractRQDto rhs = (UpdateACContractRQDto) other;
//
//        return new EqualsBuilder().append(id, rhs.id)
//                                  .append(title, rhs.title)
//                                  .append(description, rhs.description)
//                                  .append(statusDetails, rhs.statusDetails)
//                                  .append(classification, rhs.classification)
//                                  .append(period, rhs.period)
//                                  .append(dateSigned, rhs.dateSigned)
//                                  .append(documents, rhs.documents)
//                                  .append(relatedProcesses, rhs.relatedProcesses)
//                                  .append(amendments, rhs.amendments)
//                                  .isEquals();
//    }
}
