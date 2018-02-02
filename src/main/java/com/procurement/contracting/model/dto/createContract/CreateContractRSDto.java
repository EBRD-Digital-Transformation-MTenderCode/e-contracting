package com.procurement.contracting.model.dto.createContract;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.procurement.contracting.model.dto.ContractValueDto;
import com.procurement.contracting.model.dto.ContractItemDto;
import java.util.HashMap;
import java.util.LinkedHashSet;
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
    "awardID",
    "extendsContractID",
    "title",
    "description",
    "status",
    "statusDetails",
    "value",
    "items"
})
public class CreateContractRSDto {
    @JsonProperty("id")
    @NotNull
    private final String id;

    @JsonProperty("awardID")
    @NotNull
    private final String awardID;

    @JsonProperty("extendsContractID")
    @NotNull
    private final String extendsContractID;

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

    @JsonProperty("value")
    @Valid
    @NotNull
    private final ContractValueDto value;

    @JsonProperty("items")
    @JsonDeserialize(as = LinkedHashSet.class)
    @NotEmpty
    @Valid
    private final Set<ContractItemDto> items;

    public CreateContractRSDto(@NotNull String id,
                               @NotNull String awardID,
                               @NotNull String extendsContractID,
                               @NotNull String title,
                               @NotNull String description,
                               @NotNull @Valid Status status,
                               @NotNull @Valid Status statusDetails,
                               @Valid @NotNull ContractValueDto value,
                               @NotEmpty @Valid Set<ContractItemDto> items) {
        this.id = id;
        this.awardID = awardID;
        this.extendsContractID = extendsContractID;
        this.title = title;
        this.description = description;
        this.status = status;
        this.statusDetails = statusDetails;
        this.value = value;
        this.items = items;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                                    .append(awardID)
                                    .append(extendsContractID)
                                    .append(title)
                                    .append(description)
                                    .append(status)
                                    .append(statusDetails)
                                    .append(value)
                                    .append(items)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof CreateContractRSDto)) {
            return false;
        }
        final CreateContractRSDto rhs = (CreateContractRSDto) other;

        return new EqualsBuilder().append(id, rhs.id)
                                  .append(awardID, rhs.awardID)
                                  .append(extendsContractID, rhs.extendsContractID)
                                  .append(title, rhs.title)
                                  .append(description, rhs.description)
                                  .append(status, rhs.status)
                                  .append(statusDetails,rhs.statusDetails)
                                  .append(value, rhs.value)
                                  .append(items, rhs.items)
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
