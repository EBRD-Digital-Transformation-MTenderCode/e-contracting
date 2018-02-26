package com.procurement.contracting.model.dto.awardedContract;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
    "id",
    "name"
})
public class CreateACOrganizationReferenceRQDto {
    @JsonProperty("id")
    @NotNull
    private final String id;

    @JsonProperty("name")
    @NotNull
    private final String name;

    @JsonCreator
    public CreateACOrganizationReferenceRQDto(@JsonProperty("name") @NotNull final String name,
                                              @JsonProperty("id") @NotNull final String id) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name)
                                    .append(id)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof CreateACOrganizationReferenceRQDto)) {
            return false;
        }
        final CreateACOrganizationReferenceRQDto rhs = (CreateACOrganizationReferenceRQDto) other;
        return new EqualsBuilder().append(name, rhs.name)
                                  .append(id, rhs.id)
                                  .isEquals();
    }
}
