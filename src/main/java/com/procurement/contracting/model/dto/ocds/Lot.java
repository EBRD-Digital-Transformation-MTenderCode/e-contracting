package com.procurement.contracting.model.dto.ocds;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "title",
        "description",
        "status",
        "statusDetails"
})
public class Lot {
    @NotNull
    @JsonProperty("id")
    private String id;
    @NotNull
    @JsonProperty("title")
    private final String title;
    @NotNull
    @JsonProperty("description")
    private final String description;
    @JsonProperty("status")
    private TenderStatus status;
    @JsonProperty("statusDetails")
    private TenderStatusDetails statusDetails;


    @JsonCreator
    public Lot(@JsonProperty("id") final String id,
               @JsonProperty("title") final String title,
               @JsonProperty("description") final String description,
               @JsonProperty("status") final TenderStatus status,
               @JsonProperty("statusDetails") final TenderStatusDetails statusDetails) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.statusDetails = statusDetails;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                .append(title)
                .append(description)
                .append(status)
                .append(statusDetails)
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
                .append(statusDetails, rhs.statusDetails)
                .isEquals();
    }
}
