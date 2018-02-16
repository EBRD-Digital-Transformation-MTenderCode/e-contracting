
package com.procurement.contracting.model.dto.createAC;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.sun.istack.internal.NotNull;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
    "scheme",
    "description",
    "id"
})
public class CreateACClassificationDto {
    @JsonProperty("scheme")
    @NotNull
    private final Scheme scheme;

    @JsonProperty("description")
    @NotNull
    private final String description;

    @JsonProperty("id")
    @NotNull
    private final String id;

    @JsonCreator
    public CreateACClassificationDto(@JsonProperty("scheme") @NotNull final Scheme scheme,
                                     @JsonProperty("id") @NotNull final String id,
                                     @JsonProperty("description") @NotNull final String description
    ) {
        this.id = id;
        this.description = description;
        this.scheme = scheme;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(scheme)
                                    .append(id)
                                    .append(description)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof CreateACClassificationDto)) {
            return false;
        }
        final CreateACClassificationDto rhs = (CreateACClassificationDto) other;
        return new EqualsBuilder().append(scheme, rhs.scheme)
                                  .append(id, rhs.id)
                                  .append(description, rhs.description)
                                  .isEquals();
    }

    public enum Scheme {
        CPV("CPV"),
        CPVS("CPVS"),
        GSIN("GSIN"),
        UNSPSC("UNSPSC"),
        CPC("CPC"),
        OKDP("OKDP"),
        OKPD("OKPD");

        private final String value;
        private final static Map<String, Scheme> CONSTANTS = new HashMap<>();

        static {
            for (final Scheme c : values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Scheme(final String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        public static Scheme fromValue(final String value) {
            final Scheme constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            }
            return constant;
        }
    }
}
