
package com.procurement.contracting.model.dto.updateCA;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.procurement.contracting.model.dto.ContractDocumentDto;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
    "transactions",
    "documents"
})
public class UpdateContractImplementationRQDto {
    @JsonProperty("transactions")
    @JsonDeserialize(as = LinkedHashSet.class)
    @NotNull
    @Valid
    private final Set<UpdateContractTransactionRQDto> transactions;

    @JsonProperty("documents")
    @JsonDeserialize(as = LinkedHashSet.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Valid
    private final Set<ContractDocumentDto> documents;

    public UpdateContractImplementationRQDto(@JsonProperty("transactions")
                                             @NotNull
                                             @Valid
                                             final LinkedHashSet<UpdateContractTransactionRQDto> transactions,
                                             @JsonProperty("documents")
                                             @JsonInclude(JsonInclude.Include.NON_NULL)
                                             @Valid
                                             final LinkedHashSet<ContractDocumentDto> documents) {
        this.transactions = transactions;
        this.documents = documents;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(transactions)
                                    .append(documents)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UpdateContractImplementationRQDto)) {
            return false;
        }
        final UpdateContractImplementationRQDto rhs = (UpdateContractImplementationRQDto) other;
        return new EqualsBuilder().append(transactions, rhs.transactions)
                                  .append(documents, rhs.documents)
                                  .isEquals();
    }
}
