package com.procurement.contracting.model.dto.updateCA;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.contracting.databind.LocalDateTimeDeserializer;
import com.procurement.contracting.databind.LocalDateTimeSerializer;
import com.procurement.contracting.model.dto.ContractBudgetSourceDto;
import com.procurement.contracting.model.dto.ContractDocumentDto;
import com.procurement.contracting.model.dto.ContractPeriodDto;
import com.procurement.contracting.model.dto.ContractValueDto;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
    "budgetSource",
    "title",
    "description",
    "status",
    "statusDetails",
    "period",
    "value",
    "dateSigned",
    "documents",
    "relatedProcesses",
    "amendments"
})
public class UpdateContractRSDto {
    @JsonProperty("id")
    @NotNull
    private final String id;

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
    private final Status status;

    @JsonProperty("statusDetails")
    @NotNull
    @Valid
    private final Status statusDetails;

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


    @JsonProperty("relatedProcesses")
    @JsonDeserialize(as = LinkedHashSet.class)
    @NotNull
    @Valid
    private final Set<UpdateContractRelatedProcessRQDto> relatedProcesses;

    @JsonProperty("amendments")
    @NotNull
    @Valid
    private final List<UpdateContractAmendmentRQDto> amendments;

    public UpdateContractRSDto(@JsonProperty("id")
                               @NotNull
                               final String id,
                               @JsonProperty("budgetSource")
                               @NotEmpty
                               @Valid
                               final ContractBudgetSourceDto budgetSource,
                               @JsonProperty("title")
                               @NotNull
                               final String title,
                               @JsonProperty("description")
                               @NotNull
                               final String description,
                               @JsonProperty("status")
                               @NotNull
                               @Valid
                               final Status status,
                               @JsonProperty("statusDetails")
                               @NotNull
                               @Valid
                               final Status statusDetails,
                               @JsonProperty("period")
                               @Valid
                               @NotNull
                               final ContractPeriodDto period,
                               @JsonProperty("value")
                               @Valid
                               @NotNull
                               final ContractValueDto value,
                               @JsonProperty("dateSigned")
                               @JsonDeserialize(using = LocalDateTimeDeserializer.class)
                               @JsonInclude(JsonInclude.Include.NON_NULL)
                               final LocalDateTime dateSigned,
                               @JsonProperty("documents")
                               @JsonInclude(JsonInclude.Include.NON_NULL)
                               @Valid
                               final LinkedHashSet<ContractDocumentDto> documents,
                               @JsonProperty("relatedProcesses")
                               @NotNull
                               @Valid
                               final LinkedHashSet<UpdateContractRelatedProcessRQDto> relatedProcesses,
                               @JsonProperty("amendments")
                               @NotNull
                               @Valid
                               final List<UpdateContractAmendmentRQDto> amendments) {
        this.id = id;
        this.budgetSource = budgetSource;
        this.title = title;
        this.description = description;
        this.status=status;
        this.statusDetails = statusDetails;
        this.period = period;
        this.value = value;
        this.dateSigned = dateSigned;
        this.documents = documents;
        this.relatedProcesses = relatedProcesses;
        this.amendments = amendments;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                                    .append(title)
                                    .append(description)
                                    .append(status)
                                    .append(statusDetails)
                                    .append(period)
                                    .append(value)
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
        if (!(other instanceof UpdateContractRSDto)) {
            return false;
        }
        final UpdateContractRSDto rhs = (UpdateContractRSDto) other;

        return new EqualsBuilder().append(id, rhs.id)
                                  .append(title, rhs.title)
                                  .append(description, rhs.description)
                                  .append(status,rhs.status)
                                  .append(statusDetails, rhs.statusDetails)
                                  .append(period, rhs.period)
                                  .append(value, rhs.value)
                                  .append(dateSigned, rhs.dateSigned)
                                  .append(documents, rhs.documents)
                                  .append(relatedProcesses, rhs.relatedProcesses)
                                  .append(amendments, rhs.amendments)
                                  .isEquals();
    }

    public enum Status {
        PENDING("pending"),
        ACTIVE("active"),
        CANCELLED("cancelled"),
        TERMINATED("terminated");

        private final String value;
        private final static Map<String, Status> CONSTANTS = new HashMap<>();

        static {
            for (final Status c : values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        Status(final String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        public static Status fromValue(final String value) {
            final Status constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            }
            return constant;
        }
    }
}
