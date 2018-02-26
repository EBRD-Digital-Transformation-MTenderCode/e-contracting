package com.procurement.contracting.model.dto.awardedContract;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.procurement.contracting.model.dto.ContractAddressDto;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
    "address",
    "description"
})
public class CreateACPlaceOfPerformanceRQDto {
    @JsonProperty("address")
    @NotNull
    @Valid
    private final ContractAddressDto address;

    @JsonProperty("description")
    @NotNull
    private final String description;

    public CreateACPlaceOfPerformanceRQDto(@JsonProperty("address") @NotNull @Valid final ContractAddressDto address,
                                           @JsonProperty("description") @NotNull final String description) {
        this.address = address;
        this.description = description;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(address)
                                    .append(description)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof CreateACPlaceOfPerformanceRQDto)) {
            return false;
        }
        final CreateACPlaceOfPerformanceRQDto rhs = (CreateACPlaceOfPerformanceRQDto) other;
        return new EqualsBuilder().append(address, rhs.address)
                                  .append(description, rhs.description)
                                  .isEquals();
    }
}
