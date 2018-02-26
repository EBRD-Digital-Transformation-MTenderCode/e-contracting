package com.procurement.contracting.model.dto.updateAC;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
    "relationship",
    "title",
    "identifier",
    "scheme"
})
public class UpdateACRelatedProcessDto {

    @JsonProperty("relationship")
    @NotEmpty
    @Valid
    private final List<RelatedProcessType> relationship;

    @JsonProperty("title")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String title;

    @JsonProperty("identifier")
    @NotNull
    private final String identifier;

    @JsonProperty("scheme")
    @NotNull
    @Valid
    private final RelatedProcessScheme scheme;

    public UpdateACRelatedProcessDto(@JsonProperty("relationship")
                                     @NotEmpty
                                     @Valid final List<RelatedProcessType> relationship,
                                     @JsonProperty("title")
                                     @JsonInclude(JsonInclude.Include.NON_NULL) final String title,
                                     @JsonProperty("identifier")
                                     @NotNull final String identifier,
                                     @JsonProperty("scheme")
                                     @NotNull
                                     @Valid final RelatedProcessScheme scheme) {
        this.relationship = relationship;
        this.title = title;
        this.identifier = identifier;
        this.scheme = scheme;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(relationship)
                                    .append(title)
                                    .append(scheme)
                                    .append(identifier)

                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UpdateACRelatedProcessDto)) {
            return false;
        }
        final UpdateACRelatedProcessDto rhs = (UpdateACRelatedProcessDto) other;
        return new EqualsBuilder().append(relationship, rhs.relationship)
                                  .append(title, rhs.title)
                                  .append(scheme, rhs.scheme)
                                  .append(identifier, rhs.identifier)

                                  .isEquals();
    }

    public enum RelatedProcessType {
        FRAMEWORK("framework"),
        PLANNING("planning"),
        PARENT("parent"),
        PRIOR("prior"),
        UNSUCCESSFUL_PROCESS("unsuccessfulProcess"),
        SUB_CONTRACT("subContract"),
        REPLACEMENT_PROCESS("replacementProcess"),
        RENEWAL_PROCESS("renewalProcess");

        private static final Map<String, RelatedProcessType> CONSTANTS = new HashMap<>();

        static {
            for (final RelatedProcessType c : values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private final String value;

        RelatedProcessType(final String value) {
            this.value = value;
        }

        @JsonCreator
        public static RelatedProcessType fromValue(final String value) {
            final RelatedProcessType constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            }
            return constant;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }
    }

    public enum RelatedProcessScheme {
        OCID("ocid");

        private static final Map<String, RelatedProcessScheme> CONSTANTS = new HashMap<>();

        static {
            for (final RelatedProcessScheme c : values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private final String value;

        RelatedProcessScheme(final String value) {
            this.value = value;
        }

        @JsonCreator
        public static RelatedProcessScheme fromValue(final String value) {
            final RelatedProcessScheme constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            }
            return constant;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }
    }
}
