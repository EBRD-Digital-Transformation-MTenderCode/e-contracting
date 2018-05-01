package com.procurement.contracting.model.dto.ocds;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
        "budgetBreakdownID",
        "amount"
})
public class BudgetSource {

    @NotNull
    @JsonProperty("budgetBreakdownID")
    private final String id;

    @NotNull
    @JsonProperty("amount")
    private final Double amount;

    @JsonCreator
    public BudgetSource(@JsonProperty("budgetBreakdownID") final String id,
                        @JsonProperty("amount") final Double amount) {
        this.id = id;
        this.amount = amount;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(amount)
                .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof BudgetSource)) {
            return false;
        }
        final BudgetSource rhs = (BudgetSource) other;
        return new EqualsBuilder()
                .append(id, rhs.id)
                .append(amount, rhs.amount)
                .isEquals();
    }
}