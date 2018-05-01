package com.procurement.contracting.model.dto.ocds;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "rationale",
        "description"
})
public class Amendment {

    @NotNull
    @JsonProperty("rationale")
    private final String rationale;

    @JsonProperty("description")
    private final String description;

    public Amendment(@JsonProperty("rationale") final String rationale,
                     @JsonProperty("description") final String description) {
        this.rationale = rationale;
        this.description = description;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(description)
                .append(rationale)
                .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Amendment)) {
            return false;
        }
        final Amendment rhs = (Amendment) other;
        return new EqualsBuilder()
                .append(description, rhs.description)
                .append(rationale, rhs.rationale)
                .isEquals();
    }
}