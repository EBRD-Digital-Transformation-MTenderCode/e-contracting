
package com.procurement.contracting.model.dto.createContract;

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
public class Lot {
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
    private final PlaceOfPerformance placeOfPerformance;

    public Lot(@JsonProperty("id") @NotNull final String id,
               @JsonProperty("title") @NotNull final String title,
               @JsonProperty("description") @NotNull final String description,
               @JsonProperty("status") @NotNull @Valid final TenderStatus status,
               @JsonProperty("placeOfPerformance") @NotNull @Valid final PlaceOfPerformance placeOfPerformance) {
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
        if (!(other instanceof Lot)) {
            return false;
        }
        final Lot rhs = (Lot) other;
        return new EqualsBuilder().append(id, rhs.id)
                                  .append(title, rhs.title)
                                  .append(description, rhs.description)
                                  .append(status, rhs.status)
                                  .append(placeOfPerformance, rhs.placeOfPerformance)
                                  .isEquals();
    }
}
