package com.procurement.contracting.model.dto.old;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
        "scheme",
        "description",
        "id"
})
public class ClassificationDto {
//    @JsonProperty("scheme")
//    @NotNull
//    @JsonView({View.CreateACView.class, View.UpdateACView.class})
//    private final Scheme scheme;
//
//    @JsonProperty("description")
//    @NotNull
//    @JsonView({View.CreateACView.class, View.UpdateACView.class})
//    private final String description;
//
//    @JsonProperty("id")
//    @NotNull
//    @JsonView({View.CreateACView.class, View.UpdateACView.class})
//    private final String id;
//
//    @JsonCreator
//    public ClassificationDto(@JsonProperty("scheme") @NotNull final Scheme scheme,
//                             @JsonProperty("id") @NotNull final String id,
//                             @JsonProperty("description") @NotNull final String description
//    ) {
//        this.id = id;
//        this.description = description;
//        this.scheme = scheme;
//    }
//
//    @Override
//    public int hashCode() {
//        return new HashCodeBuilder().append(scheme)
//                .append(id)
//                .append(description)
//                .toHashCode();
//    }
//
//    @Override
//    public boolean equals(final Object other) {
//        if (other == this) {
//            return true;
//        }
//        if (!(other instanceof ClassificationDto)) {
//            return false;
//        }
//        final ClassificationDto rhs = (ClassificationDto) other;
//        return new EqualsBuilder().append(scheme, rhs.scheme)
//                .append(id, rhs.id)
//                .append(description, rhs.description)
//                .isEquals();
//    }
//
//    public enum Scheme {
//        CPV("CPV"),
//        CPVS("CPVS"),
//        GSIN("GSIN"),
//        UNSPSC("UNSPSC"),
//        CPC("CPC"),
//        OKDP("OKDP"),
//        OKPD("OKPD");
//
//        private static final Map<String, Scheme> CONSTANTS = new HashMap<>();
//
//        static {
//            for (final Scheme c : values()) {
//                CONSTANTS.put(c.value, c);
//            }
//        }
//
//        private final String value;
//
//        Scheme(final String value) {
//            this.value = value;
//        }
//
//        @JsonCreator
//        public static Scheme fromValue(final String value) {
//            final Scheme constant = CONSTANTS.get(value);
//            if (constant == null) {
//                throw new IllegalArgumentException(value);
//            }
//            return constant;
//        }
//
//        @Override
//        public String toString() {
//            return this.value;
//        }
//
//        @JsonValue
//        public String value() {
//            return this.value;
//        }
//    }
}
