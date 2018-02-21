package com.procurement.contracting.model.dto.changeStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.contracting.databind.LocalDateTimeDeserializer;
import com.procurement.contracting.databind.LocalDateTimeSerializer;
import com.procurement.contracting.model.dto.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
    "id",
    "awardId",
    "budgetSource",
    "title",
    "description",
    "status",
    "statusDetails",
    "classification",
    "period",
    "value",
    "dateSigned",
    "documents",
    "amendments"
})
public class ChangeStatusContractRSDto {
    @JsonProperty("id")
    @NotNull
    private final String id;

    @JsonProperty("awardId")
    @NotNull
    private final String awardId;

    @JsonProperty("budgetSource")
    @NotEmpty
    @Valid
    private final ContractBudgetSourceDto budgetSource;

    @JsonProperty("title")
    @NotNull
    private final String title;

    @JsonProperty("description")
    @NotNull
    private final String description;

    @JsonProperty("status")
    @NotNull
    @Valid
    private final ContractStatus status;

    @JsonProperty("statusDetails")
    @NotNull
    @Valid
    private final ContractStatusDetails statusDetails;

    @JsonProperty("classification")
    @NotNull
    @Valid
    private final ClassificationDto classification;

    @JsonProperty("period")
    @Valid
    @NotNull
    private final ContractPeriodDto period;

    @JsonProperty("value")
    @Valid
    @NotNull
    private final ContractValueDto value;

    @JsonProperty("dateSigned")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final LocalDateTime dateSigned;

    @JsonProperty("documents")
    @JsonDeserialize(as = LinkedHashSet.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Valid
    private final Set<ContractDocumentDto> documents;

    @JsonProperty("amendments")
    @NotNull
    @Valid
    private final List<AmendmentDto> amendments;

    public ChangeStatusContractRSDto(@JsonProperty("id")
                                 @NotNull final String id,
                                     @JsonProperty("awardId")
                                 @NotNull final String awardId,
                                     @JsonProperty("budgetSource")
                                 @NotEmpty
                                 @Valid final ContractBudgetSourceDto budgetSource,
                                     @JsonProperty("title")
                                 @NotNull final String title,
                                     @JsonProperty("description")
                                 @NotNull final String description,
                                     @JsonProperty("status")
                                 @NotNull
                                 @Valid final ContractStatus status,
                                     @JsonProperty("statusDetails")
                                 @NotNull
                                 @Valid final ContractStatusDetails statusDetails,
                                     @JsonProperty("classification")
                                 @NotNull
                                 @Valid
                                 final ClassificationDto classification,
                                     @JsonProperty("period")
                                 @Valid
                                 @NotNull final ContractPeriodDto period,
                                     @JsonProperty("value")
                                 @Valid
                                 @NotNull final ContractValueDto value,
                                     @JsonProperty("dateSigned")
                                 @JsonDeserialize(using = LocalDateTimeDeserializer.class)
                                 @JsonInclude(JsonInclude.Include.NON_NULL) final LocalDateTime dateSigned,
                                     @JsonProperty("documents")
                                 @JsonInclude(JsonInclude.Include.NON_NULL)
                                 @Valid final LinkedHashSet<ContractDocumentDto> documents,
                                     @JsonProperty("amendments")
                                 @NotNull
                                 @Valid final List<AmendmentDto> amendments) {
        this.id = id;
        this.awardId=awardId;
        this.budgetSource = budgetSource;
        this.title = title;
        this.description = description;
        this.status = status;
        this.statusDetails = statusDetails;
        this.classification=classification;
        this.period = period;
        this.value = value;
        this.dateSigned = dateSigned;
        this.documents = documents;
        this.amendments = amendments;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                                    .append(awardId)
                                    .append(title)
                                    .append(description)
                                    .append(status)
                                    .append(statusDetails)
                                    .append(classification)
                                    .append(period)
                                    .append(value)
                                    .append(dateSigned)
                                    .append(documents)
                                    .append(amendments)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ChangeStatusContractRSDto)) {
            return false;
        }
        final ChangeStatusContractRSDto rhs = (ChangeStatusContractRSDto) other;

        return new EqualsBuilder().append(id, rhs.id)
                                  .append(awardId,rhs.awardId)
                                  .append(title, rhs.title)
                                  .append(description, rhs.description)
                                  .append(status, rhs.status)
                                  .append(statusDetails, rhs.statusDetails)
                                  .append(classification,rhs.classification)
                                  .append(period, rhs.period)
                                  .append(value, rhs.value)
                                  .append(dateSigned, rhs.dateSigned)
                                  .append(documents, rhs.documents)
                                  .append(amendments, rhs.amendments)
                                  .isEquals();
    }


}
