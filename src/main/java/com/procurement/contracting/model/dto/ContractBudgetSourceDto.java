package com.procurement.contracting.model.dto;

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
    "budgetBreakdownID",
    "amount"
})
public class ContractBudgetSourceDto {
    @JsonProperty("budgetBreakdownID")
    @NotNull
    @JsonView(View.UpdateACView.class)
    private final String id;

    @JsonProperty("amount")
    @NotNull
    @JsonView(View.UpdateACView.class)
    private final Double amount;

    @JsonCreator
    public ContractBudgetSourceDto(@JsonProperty("budgetBreakdownID") @NotNull final String id,
                                   @JsonProperty("amount") @NotNull final Double amount) {
        this.id = id;
        this.amount = amount;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                                    .append(amount)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ContractBudgetSourceDto)) {
            return false;
        }
        final ContractBudgetSourceDto rhs = (ContractBudgetSourceDto) other;
        return new EqualsBuilder().append(id, rhs.id)
                                  .append(amount, rhs.amount)
                                  .isEquals();
    }
}
