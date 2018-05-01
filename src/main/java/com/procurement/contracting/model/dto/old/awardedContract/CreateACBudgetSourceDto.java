package com.procurement.contracting.model.dto.old.awardedContract;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
        "budgetBreakdownID",
        "amount"
})
public class CreateACBudgetSourceDto {
//    @JsonProperty("budgetBreakdownID")
//    @NotNull
//    private final String id;
//
//    @JsonProperty("amount")
//    @NotNull
//    private final Double amount;
//
//    @JsonCreator
//    public CreateACBudgetSourceDto(@JsonProperty("budgetBreakdownID") @NotNull final String id,
//                                   @JsonProperty("amount") @NotNull final Double amount) {
//        this.id = id;
//        this.amount = amount;
//    }
//
//    @Override
//    public int hashCode() {
//        return new HashCodeBuilder().append(id)
//                                    .append(amount)
//                                    .toHashCode();
//    }
//
//    @Override
//    public boolean equals(final Object other) {
//        if (other == this) {
//            return true;
//        }
//        if (!(other instanceof CreateACBudgetSourceDto)) {
//            return false;
//        }
//        final CreateACBudgetSourceDto rhs = (CreateACBudgetSourceDto) other;
//        return new EqualsBuilder().append(id, rhs.id)
//                                  .append(amount, rhs.amount)
//                                  .isEquals();
//    }
}
