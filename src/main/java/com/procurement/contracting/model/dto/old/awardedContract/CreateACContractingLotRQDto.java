package com.procurement.contracting.model.dto.old.awardedContract;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
        "id",
        "title",
        "description"
})
public class CreateACContractingLotRQDto {
//    @JsonProperty("id")
//    @NotNull
//    private final String id;
//
//    @JsonProperty("title")
//    @NotNull
//    private final String title;
//
//    @JsonProperty("description")
//    @NotNull
//    private final String description;
//
//    public CreateACContractingLotRQDto(@JsonProperty("id") @NotNull final String id,
//                                       @JsonProperty("title") @NotNull final String title,
//                                       @JsonProperty("description") @NotNull final String description) {
//        this.id = id;
//        this.title = title;
//        this.description = description;
//    }
//
//    @Override
//    public int hashCode() {
//        return new HashCodeBuilder().append(id)
//                                    .append(title)
//                                    .append(description)
//                                    .toHashCode();
//    }
//
//    @Override
//    public boolean equals(final Object other) {
//        if (other == this) {
//            return true;
//        }
//        if (!(other instanceof CreateACContractingLotRQDto)) {
//            return false;
//        }
//        final CreateACContractingLotRQDto rhs = (CreateACContractingLotRQDto) other;
//        return new EqualsBuilder().append(id, rhs.id)
//                                  .append(title, rhs.title)
//                                  .append(description, rhs.description)
//                                  .isEquals();
//    }
}
