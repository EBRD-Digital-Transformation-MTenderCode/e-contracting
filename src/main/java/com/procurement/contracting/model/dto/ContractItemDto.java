package com.procurement.contracting.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import com.procurement.contracting.jsonview.View;
import com.procurement.contracting.model.dto.awardedContract.CreateACUnitDto;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
    "id",
    "description",
    "classification",
    "additionalClassifications",
    "quantity",
    "unit",
    "relatedLot"
})
public class ContractItemDto {
    @JsonProperty("id")
    @NotNull
    @JsonView({View.CreateACView.class, View.UpdateACView.class})
    private final String id;

    @JsonProperty("description")
    @NotNull
    @JsonView({View.CreateACView.class, View.UpdateACView.class})
    private final String description;

    @JsonProperty("classification")
    @NotNull
    @Valid
    @JsonView({View.CreateACView.class, View.UpdateACView.class})
    private final ClassificationDto classification;

    @JsonProperty("additionalClassifications")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Valid
    @JsonView({View.CreateACView.class, View.UpdateACView.class})
    private final Set<ClassificationDto> additionalClassifications;

    @JsonProperty("quantity")
    @NotNull
    @JsonView({View.CreateACView.class, View.UpdateACView.class})
    private final Double quantity;

    @JsonProperty("unit")
    @Valid
    @NotNull
    @JsonView({View.CreateACView.class, View.UpdateACView.class})
    private final CreateACUnitDto unit;

    @JsonProperty("relatedLot")
    @NotNull
    @JsonView({View.CreateACView.class, View.UpdateACView.class})
    private final String relatedLot;

    @JsonCreator
    public ContractItemDto(@JsonProperty("id") final String id,
                           @JsonProperty("description") final String description,
                           @JsonProperty("classification") final ClassificationDto classification,
                           @JsonProperty("additionalClassifications") final LinkedHashSet<ClassificationDto>
                               additionalClassifications,
                           @JsonProperty("quantity") final Double quantity,
                           @JsonProperty("unit") final CreateACUnitDto unit,
                           @JsonProperty("relatedLot") final String relatedLot) {
        this.id = id;
        this.description = description;
        this.classification = classification;
        this.additionalClassifications = additionalClassifications;
        this.quantity = quantity;
        this.unit = unit;
        this.relatedLot = relatedLot;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                                    .append(description)
                                    .append(classification)
                                    .append(additionalClassifications)
                                    .append(quantity)
                                    .append(unit)
                                    .append(relatedLot)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ContractItemDto)) {
            return false;
        }
        final ContractItemDto rhs = (ContractItemDto) other;
        return new EqualsBuilder().append(id, rhs.id)
                                  .append(description, rhs.description)
                                  .append(classification, rhs.classification)
                                  .append(additionalClassifications, rhs.additionalClassifications)
                                  .append(quantity, rhs.quantity)
                                  .append(unit, rhs.unit)
                                  .append(relatedLot, rhs.relatedLot)
                                  .isEquals();
    }
}
