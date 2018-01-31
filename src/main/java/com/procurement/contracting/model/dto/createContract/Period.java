
package com.procurement.contracting.model.dto.createContract;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.contracting.databind.LocalDateTimeDeserializer;
import com.procurement.contracting.databind.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
    "startDate",
    "endDate"
})
public class Period {
    @JsonProperty("startDate")
    @NotNull
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private final LocalDateTime startDate;

    @JsonProperty("endDate")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @NotNull
    private final LocalDateTime endDate;

    @JsonCreator
    public Period(@JsonProperty("startDate") @NotNull @JsonDeserialize(using = LocalDateTimeDeserializer.class) final
                      LocalDateTime startDate,
                  @JsonProperty("endDate") @NotNull @JsonDeserialize(using = LocalDateTimeDeserializer.class) final
                  LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(startDate)
                                    .append(endDate)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Period)) {
            return false;
        }
        final Period rhs = (Period) other;
        return new EqualsBuilder().append(startDate, rhs.startDate)
                                  .append(endDate, rhs.endDate)
                                  .isEquals();
    }
}
