package com.procurement.contracting.model.dto.ocds;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.contracting.databind.LocalDateTimeDeserializer;
import com.procurement.contracting.databind.LocalDateTimeSerializer;
import com.procurement.contracting.model.dto.old.ContractDocumentDto;
import com.procurement.contracting.model.dto.old.ContractItemDto;
import java.time.LocalDateTime;
import java.util.List;
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
        "status",
        "statusDetails",
        "title",
        "description",
        "extendsContractID",
        "budgetSource",
        "classification",
        "period",
        "value",
        "items",
        "dateSigned",
        "documents",
        "relatedProcesses",
        "amendments"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Contract {

    @NotNull
    @JsonProperty("id")
    private String id;

    @JsonProperty("token")
    private String token;

    @NotNull
    @JsonProperty("awardId")
    private String awardId;

    @Valid
    @NotNull
    @JsonProperty("status")
    private ContractStatus status;

    @Valid
    @NotNull
    @JsonProperty("statusDetails")
    private ContractStatusDetails statusDetails;

    @NotNull
    @JsonProperty("title")
    private String title;

    @NotNull
    @JsonProperty("description")
    private String description;

    @NotNull
    @JsonProperty("extendsContractID")
    private String extendsContractID;

    @Valid
    @NotEmpty
    @JsonProperty("budgetSource")
    private List<BudgetSource> budgetSource;

    @Valid
    @NotNull
    @JsonProperty("classification")
    private Classification classification;

    @Valid
    @NotNull
    @JsonProperty("period")
    private Period period;

    @Valid
    @NotNull
    @JsonProperty("value")
    private final Value value;

    @NotEmpty
    @Valid
    @JsonProperty("items")
    private List<Item> items;

    @JsonProperty("dateSigned")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime dateSigned;

    @Valid
    @JsonProperty("documents")
    private List<Document> documents;

    @NotNull
    @Valid
    @JsonProperty("relatedProcesses")
    private List<RelatedProcess> relatedProcesses;

    @NotNull
    @Valid
    @JsonProperty("amendments")
    private List<Amendment> amendments;

    public Contract(@JsonProperty("id") final String id,
                    @JsonProperty("token") final String token,
                    @JsonProperty("awardId") final String awardId,
                    @JsonProperty("status") final ContractStatus status,
                    @JsonProperty("statusDetails") final ContractStatusDetails statusDetails,
                    @JsonProperty("title") final String title,
                    @JsonProperty("description") final String description,
                    @JsonProperty("extendsContractID") final String extendsContractID,
                    @JsonProperty("budgetSource") final List<BudgetSource> budgetSource,
                    @JsonProperty("classification") final Classification classification,
                    @JsonProperty("period") final Period period,
                    @JsonProperty("value") final Value value,
                    @JsonProperty("items") final List<Item> items,
                    @JsonProperty("dateSigned") final LocalDateTime dateSigned,
                    @JsonProperty("documents") final List<Document> documents,
                    @JsonProperty("relatedProcesses") final List<RelatedProcess> relatedProcesses,
                    @JsonProperty("amendments") final List<Amendment> amendments) {
        this.id = id;
        this.token = token;
        this.awardId = awardId;
        this.status = status;
        this.statusDetails = statusDetails;
        this.title = title;
        this.description = description;
        this.extendsContractID = extendsContractID;
        this.budgetSource = budgetSource;
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
                .append(extendsContractID)
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
        if (!(other instanceof Contract)) {
            return false;
        }
        final Contract rhs = (Contract) other;

        return new EqualsBuilder()
                .append(id, rhs.id)
                .append(awardId, rhs.awardId)
                .append(extendsContractID, rhs.extendsContractID)
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
