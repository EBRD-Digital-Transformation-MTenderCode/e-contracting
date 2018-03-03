package com.procurement.contracting.model.dto.contractAwardNotice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.procurement.contracting.model.dto.ContractStatus;
import com.procurement.contracting.model.dto.ContractStatusDetails;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
    "id",
    "awardID",
    "status",
    "statusDetails"
})
public class CreateCanContractRSDto {
    @JsonProperty("id")
    @NotNull
    private final String id;

    @JsonProperty("awardID")
    @NotNull
    private final String awardID;

    @JsonProperty("status")
    @NotNull
    private final ContractStatus status;

    @JsonProperty("statusDetails")
    private final ContractStatusDetails statusDetails;

    @JsonCreator
    public CreateCanContractRSDto(@JsonProperty("id") final String id,
                                  @JsonProperty("awardID") final String awardID,
                                  @JsonProperty("status") final ContractStatus status,
                                  @JsonProperty("status") final ContractStatusDetails statusDetails) {
        this.id = id;
        this.awardID = awardID;
        this.status = status;
        this.statusDetails = statusDetails;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                                    .append(awardID)
                                    .append(status)
                                    .append(statusDetails)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof CreateCanContractRSDto)) {
            return false;
        }
        final CreateCanContractRSDto rhs = (CreateCanContractRSDto) other;

        return new EqualsBuilder().append(id, rhs.id)
                                  .append(awardID, rhs.awardID)
                                  .append(status, rhs.status)
                                  .append(statusDetails, rhs.statusDetails)
                                  .isEquals();
    }
}
