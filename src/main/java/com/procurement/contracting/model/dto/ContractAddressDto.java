package com.procurement.contracting.model.dto;

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
public class ContractAddressDto {
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

    public ContractAddressDto(@JsonProperty("streetAddress") @NotNull final String streetAddress,
                              @JsonProperty("locality") @NotNull final String locality,
                              @JsonProperty("region") @NotNull final String region,
                              @JsonProperty("postalCode") @NotNull final String postalCode,
                              @JsonProperty("countryName") @NotNull final String countryName) {
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
        if (!(other instanceof ContractAddressDto)) {
            return false;
        }
        final ContractAddressDto rhs = (ContractAddressDto) other;
        return new EqualsBuilder().append(streetAddress, rhs.streetAddress)
                                  .append(locality, rhs.locality)
                                  .append(region, rhs.region)
                                  .append(postalCode, rhs.postalCode)
                                  .append(countryName, rhs.countryName)
                                  .isEquals();
    }
}
