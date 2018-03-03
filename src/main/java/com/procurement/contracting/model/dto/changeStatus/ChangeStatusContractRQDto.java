package com.procurement.contracting.model.dto.changeStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.procurement.contracting.model.dto.AmendmentDto;
import com.procurement.contracting.model.dto.ContractDocumentDto;
import com.procurement.contracting.model.dto.ContractStatusDetails;
import java.util.LinkedHashSet;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
    "id",
    "statusDetails",
    "documents",
    "amendments"
})
public class ChangeStatusContractRQDto {
    @JsonProperty("id")
    @NotNull
    private final String id;

    @JsonProperty("statusDetails")
    @NotNull
    @Valid
    private final ContractStatusDetails statusDetails;

    @JsonProperty("documents")
    @JsonDeserialize(as = LinkedHashSet.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Valid
    private final List<ContractDocumentDto> documents;

    @JsonProperty("amendments")
    @NotNull
    @Valid
    private final List<AmendmentDto> amendments;

    public ChangeStatusContractRQDto(@JsonProperty("id") @NotNull final String id,
                                     @JsonProperty("statusDetails") @NotNull @Valid final ContractStatusDetails
                                         statusDetails,
                                     @JsonProperty("documents") final @Valid List<ContractDocumentDto> documents,
                                     @JsonProperty("amendments") final @NotNull @Valid List<AmendmentDto> amendments) {
        this.id = id;
        this.statusDetails = statusDetails;
        this.documents = documents;
        this.amendments = amendments;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                                    .append(statusDetails)
                                    .append(documents)
                                    .append(amendments)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ChangeStatusContractRQDto)) {
            return false;
        }
        final ChangeStatusContractRQDto rhs = (ChangeStatusContractRQDto) other;

        return new EqualsBuilder().append(id, rhs.id)
                                  .append(statusDetails, rhs.statusDetails)
                                  .append(documents, rhs.documents)
                                  .append(amendments, rhs.amendments)
                                  .isEquals();
    }
}
