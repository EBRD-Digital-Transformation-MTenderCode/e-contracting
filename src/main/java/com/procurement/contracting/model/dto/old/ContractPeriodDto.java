package com.procurement.contracting.model.dto.old;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({
        "startDate",
        "endDate"
})
public class ContractPeriodDto {
//    @JsonProperty("startDate")
//    @NotNull
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonView(View.UpdateACView.class)
//    private LocalDateTime startDate;
//
//    @JsonProperty("endDate")
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @NotNull
//    @JsonView(View.UpdateACView.class)
//    private LocalDateTime endDate;
//
//    @JsonCreator
//    public ContractPeriodDto(@JsonProperty("startDate")
//                             @NotNull
//                             @JsonDeserialize(using = LocalDateTimeDeserializer.class) final LocalDateTime startDate,
//                             @JsonProperty("endDate")
//                             @NotNull
//                             @JsonDeserialize(using = LocalDateTimeDeserializer.class) final LocalDateTime endDate) {
//        this.startDate = startDate;
//        this.endDate = endDate;
//    }
//
//    @Override
//    public int hashCode() {
//        return new HashCodeBuilder().append(startDate)
//                .append(endDate)
//                .toHashCode();
//    }
//
//    @Override
//    public boolean equals(final Object other) {
//        if (other == this) {
//            return true;
//        }
//        if (!(other instanceof ContractPeriodDto)) {
//            return false;
//        }
//        final ContractPeriodDto rhs = (ContractPeriodDto) other;
//        return new EqualsBuilder().append(startDate, rhs.startDate)
//                .append(endDate, rhs.endDate)
//                .isEquals();
//    }
}
