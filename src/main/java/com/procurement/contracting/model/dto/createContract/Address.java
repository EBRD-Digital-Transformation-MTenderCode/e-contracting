
package com.procurement.contracting.model.dto.createContract;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
    "streetAddress",
    "locality",
    "region",
    "postalCode",
    "countryName"
})
public class Address {
    @JsonProperty("streetAddress")
    @NotNull
    private final String streetAddress;

    @JsonProperty("locality")
    @NotNull
    private final String locality;

    @JsonProperty("region")
    @NotNull
    private final String region;

    @JsonProperty("postalCode")
    @NotNull
    private final String postalCode;

    @JsonProperty("countryName")
    @NotNull
    private final String countryName;

    public Address(@JsonProperty("streetAddress") @NotNull String streetAddress,
                   @JsonProperty("locality") @NotNull String locality,
                   @JsonProperty("region") @NotNull String region,
                   @JsonProperty("postalCode") @NotNull String postalCode,
                   @JsonProperty("countryName") @NotNull String countryName) {
        this.streetAddress = streetAddress;
        this.locality = locality;
        this.region = region;
        this.postalCode = postalCode;
        this.countryName = countryName;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(streetAddress)
                                    .append(locality)
                                    .append(region)
                                    .append(postalCode)
                                    .append(countryName)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Address)) {
            return false;
        }
        final Address rhs = (Address) other;
        return new EqualsBuilder().append(streetAddress, rhs.streetAddress)
                                  .append(locality, rhs.locality)
                                  .append(region, rhs.region)
                                  .append(postalCode, rhs.postalCode)
                                  .append(countryName, rhs.countryName)
                                  .isEquals();
    }
}
