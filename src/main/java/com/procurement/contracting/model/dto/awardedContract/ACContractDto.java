package com.procurement.contracting.model.dto.awardedContract;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.contracting.databind.LocalDateTimeDeserializer;
import com.procurement.contracting.databind.LocalDateTimeSerializer;
import com.procurement.contracting.model.dto.*;
import com.procurement.contracting.model.dto.updateAC.UpdateACRelatedProcessDto;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@Setter
@JsonPropertyOrder({
    "id",
    "awardId",
    "extendsContractId",
    "budgetSource",
    "title",
    "description",
    "status",
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
public class ACContractDto {
    @JsonProperty("value")
    @Valid
    @NotNull
    private final ContractValueDto value;
    @JsonProperty("id")
    @NotNull
    private String id;
    @JsonProperty("awardId")
    @NotNull
    private String awardId;
    @JsonProperty("extendsContractId")
    @NotNull
    private String extendsContractId;
    @JsonProperty("budgetSource")
    @NotEmpty
    @Valid
    private List<ContractBudgetSourceDto> budgetSource;
    @JsonProperty("title")
    @NotNull
    private String title;
    @JsonProperty("description")
    @NotNull
    private String description;
    @JsonProperty("status")
    @NotNull
    @Valid
    private ContractStatus status;
    @JsonProperty("statusDetails")
    @NotNull
    @Valid
    private ContractStatusDetails statusDetails;
    @JsonProperty("classification")
    @NotNull
    @Valid
    private ClassificationDto classification;
    @JsonProperty("period")
    @Valid
    @NotNull
    private ContractPeriodDto period;
    @JsonProperty("items")
    @NotEmpty
    @Valid
    private List<ContractItemDto> items;

    @JsonProperty("dateSigned")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime dateSigned;

    @JsonProperty("documents")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonDeserialize(as = LinkedHashSet.class)
    @Valid
    private List<ContractDocumentDto> documents;

    @JsonProperty("relatedProcesses")
    @JsonDeserialize(as = LinkedHashSet.class)
    @NotNull
    @Valid
    private List<UpdateACRelatedProcessDto> relatedProcesses;

    @JsonProperty("amendments")
    @NotNull
    @Valid
    private List<AmendmentDto> amendments;

    public ACContractDto(@JsonProperty("id") @NotNull final String id,
                         @JsonProperty("awardId") @NotNull final String awardId,
                         @JsonProperty("extendsContractId") @NotNull final String extendsContractId,
                         @JsonProperty("budgetSource") @NotEmpty @Valid final List<ContractBudgetSourceDto> budgetSource,
                         @JsonProperty("title") @NotNull final String title,
                         @JsonProperty("description") @NotNull final String description,
                         @JsonProperty("status") @NotNull @Valid final ContractStatus status,
                         @JsonProperty("statusDetails") @NotNull @Valid final ContractStatusDetails statusDetails,
                         @JsonProperty("classification") @NotNull @Valid final ClassificationDto classification,
                         @JsonProperty("period") @JsonInclude(JsonInclude.Include.ALWAYS) @Valid @NotNull
                         final ContractPeriodDto period,
                         @JsonProperty("value") @Valid @NotNull final ContractValueDto value,
                         @JsonProperty("items") @NotEmpty @Valid final List<ContractItemDto> items,
                         @JsonProperty("dateSigned") @JsonDeserialize(using = LocalDateTimeDeserializer.class)
                         @JsonInclude(JsonInclude.Include.NON_NULL) final LocalDateTime dateSigned,
                         @JsonProperty("documents")
                         @JsonInclude(JsonInclude.Include.NON_NULL) @Valid final List<ContractDocumentDto> documents,
                         @JsonProperty("relatedProcesses") @NotNull @Valid
                         final List<UpdateACRelatedProcessDto> relatedProcesses,
                         @JsonProperty("amendments") @NotNull @Valid final List<AmendmentDto> amendments) {
        this.id = id;
        this.awardId = awardId;
        this.extendsContractId = extendsContractId;
        this.budgetSource = budgetSource;
        this.title = title;
        this.description = description;
        this.status = status;
        this.statusDetails = statusDetails;
        this.classification = classification;
        this.period = period;
        this.value = value;
        this.items = items;
        this.dateSigned = dateSigned;
        this.documents = documents;
        this.relatedProcesses = relatedProcesses;
        this.amendments = amendments;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                                    .append(awardId)
                                    .append(extendsContractId)
                                    .append(budgetSource)
                                    .append(title)
                                    .append(description)
                                    .append(status)
                                    .append(statusDetails)
                                    .append(classification)
                                    .append(period)
                                    .append(value)
                                    .append(items)
                                    .append(dateSigned)
                                    .append(documents)
                                    .append(relatedProcesses)
                                    .append(amendments)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ACContractDto)) {
            return false;
        }
        final ACContractDto rhs = (ACContractDto) other;

        return new EqualsBuilder().append(id, rhs.id)
                                  .append(awardId, rhs.awardId)
                                  .append(extendsContractId, rhs.extendsContractId)
                                  .append(budgetSource, rhs.budgetSource)
                                  .append(title, rhs.title)
                                  .append(description, rhs.description)
                                  .append(status, rhs.status)
                                  .append(statusDetails, rhs.statusDetails)
                                  .append(classification, rhs.classification)
                                  .append(period, rhs.period)
                                  .append(value, rhs.value)
                                  .append(items, rhs.items)
                                  .append(dateSigned, rhs.dateSigned)
                                  .append(documents, rhs.documents)
                                  .append(relatedProcesses, rhs.relatedProcesses)
                                  .append(amendments, rhs.amendments)
                                  .isEquals();
    }
}
