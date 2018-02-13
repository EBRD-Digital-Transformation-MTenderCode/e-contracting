package com.procurement.contracting.model.dto.updateCA;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.procurement.contracting.model.dto.ContractAddressDto;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
    "name",
    "id",
    "identifier",
    "additionalIdentifiers",
    "address",
    "contactPoint"
})
public class UpdateContractOrganizationRQDto {
    @JsonProperty("name")
    @NotNull
    private final String name;

    @JsonProperty("id")
    @NotNull
    private final String id;

    @JsonProperty("identifier")
    @NotNull
    @Valid
    private final UpdateContractIdentifierRQDto identifier;

    @JsonProperty("additionalIdentifiers")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Valid
    private final Set<UpdateContractIdentifierRQDto> additionalIdentifiers;

    @JsonProperty("address")
    @NotNull
    @Valid
    private final ContractAddressDto address;

    @JsonProperty("contactPoint")
    @NotNull
    @Valid
    private final UpdateContactPointRQDto contactPoint;

    public UpdateContractOrganizationRQDto(@JsonProperty("name") @NotNull final String name,
                                           @JsonProperty("id") @NotNull final String id,
                                           @JsonProperty("identifier")
                                           @NotNull
                                           @Valid
                                           final UpdateContractIdentifierRQDto identifier,
                                           @JsonProperty("additionalIdentifiers")
                                           @JsonInclude(JsonInclude.Include.NON_NULL)
                                           @Valid
                                           final LinkedHashSet<UpdateContractIdentifierRQDto> additionalIdentifiers,
                                           @JsonProperty("address")
                                           @NotNull
                                           @Valid
                                           final ContractAddressDto address,
                                           @JsonProperty("contactPoint")
                                           @NotNull
                                           @Valid
                                           final UpdateContactPointRQDto contactPoint) {
        this.name = name;
        this.id = id;
        this.identifier = identifier;
        this.additionalIdentifiers = additionalIdentifiers;
        this.address = address;
        this.contactPoint = contactPoint;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name)
                                    .append(id)
                                    .append(identifier)
                                    .append(additionalIdentifiers)
                                    .append(address)
                                    .append(contactPoint)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UpdateContractOrganizationRQDto)) {
            return false;
        }
        final UpdateContractOrganizationRQDto rhs = (UpdateContractOrganizationRQDto) other;
        return new EqualsBuilder().append(name, rhs.name)
                                  .append(id, rhs.id)
                                  .append(identifier, rhs.identifier)
                                  .append(additionalIdentifiers, rhs.additionalIdentifiers)
                                  .append(address, rhs.address)
                                  .append(contactPoint, rhs.contactPoint)
                                  .isEquals();
    }
}
