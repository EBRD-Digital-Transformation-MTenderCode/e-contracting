package com.procurement.contracting.model.dto.awardedContract;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import com.procurement.contracting.jsonview.View;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
    "id",
    "name"
})
public class CreateACUnitDto {
    @JsonProperty("id")
    @NotNull
    @JsonView({View.CreateACView.class, View.UpdateACView.class})
    private final String id;

    @JsonProperty("name")
    @NotNull
    @JsonView({View.CreateACView.class, View.UpdateACView.class})
    private final String name;

    @JsonCreator
    public CreateACUnitDto(@JsonProperty("name") final String name,
                           @JsonProperty("id") final String id) {
        this.name = name;
        this.id = id;
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
        if (!(other instanceof CreateACUnitDto)) {
            return false;
        }
        final CreateACUnitDto rhs = (CreateACUnitDto) other;
        return new EqualsBuilder().append(name, rhs.name)
                                  .append(id, rhs.id)
                                  .isEquals();
    }
}
