
package com.procurement.contracting.model.dto.updateCA;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
    "rationale",
    "id",
    "description"
})
public class UpdateContractAmendmentRQDto {

    @JsonProperty("rationale")
    @NotNull
    private final String rationale;

    @JsonProperty("description")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String description;

    @JsonProperty("id")
    @NotNull
    private final String id;

    public UpdateContractAmendmentRQDto(@JsonProperty("rationale")
                                        @NotNull final String rationale,
                                        @JsonProperty("id")
                                        @NotNull final String id,
                                        @JsonProperty("description")
                                        @JsonInclude(JsonInclude.Include.NON_NULL)
                                        final String description) {
        this.rationale = rationale;
        this.id = id;
        this.description = description;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                                    .append(description)
                                    .append(rationale)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UpdateContractAmendmentRQDto)) {
            return false;
        }
        final UpdateContractAmendmentRQDto rhs = (UpdateContractAmendmentRQDto) other;
        return new EqualsBuilder().append(id, rhs.id)
                                  .append(description, rhs.description)
                                  .append(rationale, rhs.rationale)
                                  .isEquals();
    }
}
