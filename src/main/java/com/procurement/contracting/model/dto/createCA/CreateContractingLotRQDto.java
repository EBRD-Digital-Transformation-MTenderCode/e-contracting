
package com.procurement.contracting.model.dto.createCA;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
    "id",
    "title",
    "description",
    "status",
    "placeOfPerformance"
})
public class CreateContractingLotRQDto {
    @JsonProperty("id")
    @NotNull
    private final String id;

    @JsonProperty("title")
    @NotNull
    private final String title;

    @JsonProperty("description")
    @NotNull
    private final String description;

    @JsonProperty("status")
    @NotNull
    @Valid
    private final TenderStatus status;

    @JsonProperty("placeOfPerformance")
    @NotNull
    @Valid
    private final CreateContractPlaceOfPerformanceRQDto placeOfPerformance;

    public CreateContractingLotRQDto(@JsonProperty("id") @NotNull final String id,
                                     @JsonProperty("title") @NotNull final String title,
                                     @JsonProperty("description") @NotNull final String description,
                                     @JsonProperty("status") @NotNull @Valid final TenderStatus status,
                                     @JsonProperty("placeOfPerformance") @NotNull @Valid final CreateContractPlaceOfPerformanceRQDto placeOfPerformance) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.placeOfPerformance = placeOfPerformance;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                                    .append(title)
                                    .append(description)
                                    .append(status)
                                    .append(placeOfPerformance)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof CreateContractingLotRQDto)) {
            return false;
        }
        final CreateContractingLotRQDto rhs = (CreateContractingLotRQDto) other;
        return new EqualsBuilder().append(id, rhs.id)
                                  .append(title, rhs.title)
                                  .append(description, rhs.description)
                                  .append(status, rhs.status)
                                  .append(placeOfPerformance, rhs.placeOfPerformance)
                                  .isEquals();
    }
}
