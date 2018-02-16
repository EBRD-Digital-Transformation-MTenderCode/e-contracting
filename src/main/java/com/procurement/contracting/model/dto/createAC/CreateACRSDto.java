package com.procurement.contracting.model.dto.createAC;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.procurement.contracting.model.dto.ContractItemDto;
import com.procurement.contracting.model.dto.ContractStatus;
import com.procurement.contracting.model.dto.ContractStatusDetails;
import com.procurement.contracting.model.dto.ContractValueDto;
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
    "awardID",
    "extendsContractID",
    "title",
    "description",
    "status",
    "statusDetails",
    "value",
    "items"
})
public class CreateACRSDto {
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
    private final ContractStatus status;

    @JsonProperty("statusDetails")
    @NotNull
    @Valid
    private final ContractStatusDetails statusDetails;

    @JsonProperty("value")
    @Valid
    @NotNull
    private final ContractValueDto value;

    @JsonProperty("items")
    @NotEmpty
    @Valid
    private final List<ContractItemDto> items;

    public CreateACRSDto(@NotNull String id,
                         @NotNull String awardID,
                         @NotNull String extendsContractID,
                         @NotNull String title,
                         @NotNull String description,
                         @NotNull @Valid ContractStatus status,
                         @NotNull @Valid ContractStatusDetails statusDetails,
                         @Valid @NotNull ContractValueDto value,
                         @NotEmpty @Valid List<ContractItemDto> items) {
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
        if (!(other instanceof CreateACRSDto)) {
            return false;
        }
        final CreateACRSDto rhs = (CreateACRSDto) other;

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


}
