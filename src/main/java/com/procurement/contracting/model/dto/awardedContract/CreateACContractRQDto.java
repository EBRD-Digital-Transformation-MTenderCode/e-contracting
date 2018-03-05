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
    "awardID"
})
public class CreateACContractRQDto {

    @JsonProperty("id")
    @NotNull
    private final String id;

    @JsonProperty("awardID")
    @NotNull
    private final String awardID;

    @JsonCreator
    public CreateACContractRQDto(@JsonProperty("id")
                                 @NotNull final String id,
                                 @JsonProperty("awardID") @NotNull final String awardID) {

        this.id = id;
        this.awardID = awardID;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                                    .append(awardID)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof CreateACContractRQDto)) {
            return false;
        }
        final CreateACContractRQDto rhs = (CreateACContractRQDto) other;

        return new EqualsBuilder().append(id, rhs.id)
                                  .append(awardID, rhs.awardID)
                                  .isEquals();
    }
}