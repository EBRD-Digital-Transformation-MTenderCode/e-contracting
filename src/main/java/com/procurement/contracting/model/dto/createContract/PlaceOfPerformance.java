
package com.procurement.contracting.model.dto.createContract;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
public class PlaceOfPerformance {
    @JsonProperty("address")
    @NotNull
    @Valid
    private final Address address;

    @JsonProperty("description")
    @NotNull
    private final String description;

    public PlaceOfPerformance(@JsonProperty("address") @NotNull @Valid final Address address,
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
        if (!(other instanceof PlaceOfPerformance)) {
            return false;
        }
        final PlaceOfPerformance rhs = (PlaceOfPerformance) other;
        return new EqualsBuilder().append(address, rhs.address)
                                  .append(description, rhs.description)
                                  .isEquals();
    }
}
