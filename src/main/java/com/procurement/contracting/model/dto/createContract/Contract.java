package com.procurement.contracting.model.dto.createContract;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.contracting.databind.LocalDateTimeDeserializer;
import com.procurement.contracting.databind.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
    "budgetSource",
    "awardID",
    "extendsContractID",
    "title",
    "description",
    "classification",
    "period",
    "dateSigned",
    "documents"
})
public class Contract {

    @JsonProperty("budgetSource")
    @NotEmpty
    @Valid
    private final List<BudgetBreakdown> budgetBreakdown;

    @JsonProperty("awardID")
    @NotNull
    private final String awardID;

    @JsonProperty("extendsContractID")
    @NotNull
    private final String extendsContractID;

    @JsonProperty("title")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String title;

    @JsonProperty("description")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String description;

    @JsonProperty("classification")
    @NotNull
    @Valid
    private final Classification classification;

    @JsonProperty("period")
    @NotNull
    @Valid
    private final Period period;

    @JsonProperty("dateSigned")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private final LocalDateTime dateSigned;

    @JsonProperty("documents")
    @Valid
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final List<Document> documents;

    @JsonCreator
    public Contract(@JsonProperty("budgetSource") @NotEmpty @Valid final List<BudgetBreakdown> budget,
                    @JsonProperty("awardID") @NotNull final String awardID,
                    @JsonProperty("extendsContractID") @NotNull final String extendsContractID,
                    @JsonProperty("title") @NotNull final String title,
                    @JsonProperty("description") @NotNull final String description,
                    @JsonProperty("classification") @NotNull @Valid final Classification classification,
                    @JsonProperty("period") @NotNull @Valid final Period period,
                    @JsonProperty("dateSigned")@NotNull @JsonDeserialize(using = LocalDateTimeDeserializer.class) final LocalDateTime dateSigned,
                    @JsonProperty("documents")@Valid @JsonInclude(JsonInclude.Include.NON_NULL) final List<Document> documents) {

        this.budgetBreakdown = budget;
        this.awardID = awardID;
        this.extendsContractID = extendsContractID;
        this.title = title;
        this.description = description;
        this.classification = classification;
        this.period = period;
        this.dateSigned = dateSigned;
        this.documents = documents;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(budgetBreakdown)
                                    .append(awardID)
                                    .append(extendsContractID)
                                    .append(title)
                                    .append(description)
                                    .append(classification)
                                    .append(period)
                                    .append(dateSigned)
                                    .append(documents)
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

        return new EqualsBuilder().append(budgetBreakdown, rhs.budgetBreakdown)
                                  .append(awardID, rhs.awardID)
                                  .append(extendsContractID, rhs.extendsContractID)
                                  .append(title, rhs.title)
                                  .append(description, rhs.description)
                                  .append(classification, rhs.classification)
                                  .append(period, rhs.period)
                                  .append(dateSigned, rhs.dateSigned)
                                  .append(documents, rhs.documents)
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
